package travel.travel.bookmark.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.travel.bookmark.domain.Bookmark;
import travel.travel.bookmark.repository.BookmarkRepository;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.PlanRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PlanRepository planRepository;
    
    public void addBookmark(Long planId, Long memberId) {
        Member member = getMember(memberId);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByMemberAndPlan(member, plan);
        if (existingBookmark.isEmpty()) {
            Bookmark bookmark = Bookmark.builder()
                    .member(member)
                    .plan(plan)
                    .build();
            bookmarkRepository.save(bookmark);
        } else {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }
    }

    public void removeBookmark(Long planId, Long memberId) {
        Member member = getMember(memberId);

        Plan Plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByMemberAndPlan(member, Plan);
        existingBookmark.ifPresent(bookmarkRepository::delete);
    }

    public long getBookmarkCount(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));
        return bookmarkRepository.countByPlan(plan);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
