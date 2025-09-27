package travel.travel.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel.bookmark.domain.Bookmark;
import travel.travel.bookmark.repository.BookmarkRepository;
import travel.travel.common.exception.CustomErrorCode;
import travel.travel.common.exception.CustomException;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.PlanRepository;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PlanRepository planRepository;
    
    public void addBookmark(Long planId, Long memberId) {
        Member member = getMember(memberId);

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByMemberAndPlan(member, plan);
        if (existingBookmark.isEmpty()) {
            Bookmark bookmark = Bookmark.builder()
                    .member(member)
                    .plan(plan)
                    .build();
            bookmarkRepository.save(bookmark);
        } else {
            throw new CustomException(CustomErrorCode.ALREADY_PUSH);
        }
    }

    public void removeBookmark(Long planId, Long memberId) {
        Member member = getMember(memberId);

        Plan Plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByMemberAndPlan(member, Plan);
        existingBookmark.ifPresent(bookmarkRepository::delete);
    }

    public long getBookmarkCount(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));
        return bookmarkRepository.countByPlan(plan);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));
    }
}
