package travel.travel.like.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import travel.travel.comment.domain.Comment;
import travel.travel.comment.repository.CommentRepository;
import travel.travel.like.repository.LikeRepository;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LikeConcurrencyTest {

    @Autowired private LikeService likeService;
    @Autowired private LikeRepository likeRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private CommentRepository commentRepository;

    private Member testMember;
    private Comment testComment;

    @BeforeEach
    @Transactional
    void setUp() {
        // 테스트 데이터 조회
        testMember = memberRepository.findById(1L)
                .orElse(null);
        testComment = commentRepository.findById(1L)
                .orElse(null);
    }

    @AfterEach
    void tearDown() {
        likeRepository.deleteByCommentIdAndMemberId(testComment.getCommentId(), testMember.getId());
    }

    @Test
    @DisplayName("동시성 테스트: 같은 사용자가 동시에 여러 좋아요 요청")
    void testConcurrentLikeFromSameUser() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger completedCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    likeService.addLike(testComment.getCommentId(), testMember.getId());
                    completedCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("Unexpected Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        System.out.println("Completed requests: " + completedCount.get());
        assertThat(completedCount.get()).isEqualTo(threadCount);

        long actualLikeCount = likeRepository.countByComment(testComment);
        assertThat(actualLikeCount).isEqualTo(1);
    }


}