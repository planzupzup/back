package travel.travel.like.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import travel.travel.comment.domain.Comment;
import travel.travel.comment.repository.CommentRepository;
import travel.travel.like.domain.Like;
import travel.travel.like.repository.LikeRepository;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.repository.PlanRepository;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    public void addLike(Long commentId) {
        // String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        planRepository.findById(comment.getPlan().getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        Optional<Like> existingLike = likeRepository.findByMemberAndComment(member, comment);
        if (existingLike.isEmpty()) {
            Like like = Like.builder()
                    .member(member)
                    .comment(comment)
                    .build();
            likeRepository.save(like);
        } else {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }
    }

    public void removeLike(Long commentId) {
        // String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        planRepository.findById(comment.getPlan().getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        Optional<Like> existingLike = likeRepository.findByMemberAndComment(member, comment);
        existingLike.ifPresent(likeRepository::delete);
    }

    public long getLikeCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        planRepository.findById(comment.getPlan().getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        return likeRepository.countByComment(comment);
    }
}