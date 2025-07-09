package travel.travel.mypage;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import travel.travel.bookmark.repository.BookmarkRepository;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.dto.PlanThumbResDto;
import travel.travel.plan.repository.PlanRepository;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final BookmarkRepository bookmarkRepository;

    public String updateNickName(NickNameReqDto nickNameReqDto) {
        Member member = getMember();
        member.updateNickName(nickNameReqDto.getNickName());
        return nickNameReqDto.getNickName();
    }

    public PageApiResponse<PlanThumbResDto> getBookmarkedPlans(int page, int size) {
        Member member = getMember();
        Pageable pageable = PageRequest.of(page, size);
        Page<Plan> plans = bookmarkRepository.findBookmarkedPlansByMember(member, pageable);

        List<PlanThumbResDto> content = plans.getContent().stream()
                .map(plan -> PlanThumbResDto.of(plan, true))
                .toList();

        return PageApiResponse.<PlanThumbResDto>builder()
                .content(content)
                .page(plans.getNumber())
                .size(plans.getSize())
                .totalPages(plans.getTotalPages())
                .totalElements(plans.getTotalElements())
                .first(plans.isFirst())
                .last(plans.isLast())
                .build();
    }

    public PageApiResponse<PlanThumbResDto> getMyPlans(int page, int size) {
        Member member = getMember();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<Plan> plans = planRepository.findByMember(member, pageable);

        List<PlanThumbResDto> content = plans.getContent().stream()
                .map(plan -> PlanThumbResDto.of(plan, false))
                .toList();

        return PageApiResponse.<PlanThumbResDto>builder()
                .content(content)
                .page(plans.getNumber())
                .size(plans.getSize())
                .totalPages(plans.getTotalPages())
                .totalElements(plans.getTotalElements())
                .first(plans.isFirst())
                .last(plans.isLast())
                .build();
    }


    private Member getMember() {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        return memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
