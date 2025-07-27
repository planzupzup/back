package travel.travel.mypage;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.bookmark.repository.BookmarkRepository;
import travel.travel.comment.repository.CommentRepository;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.image.domain.Image;
import travel.travel.image.service.ImageService;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.dto.PlanThumbResDto;
import travel.travel.plan.repository.PlanRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ImageService imageService;
    private final CommentRepository commentRepository;

    public MemberResDto updateMyInfo(NickNameReqDto nickNameReqDto, MultipartFile file, Long memberId) {
        Member member = getMember(memberId);
        member.updateNickName(nickNameReqDto.getNickName());

        if (member.getImageUrl() != null) {
            imageService.deleteImages(List.of(Image.builder().imageUrl(member.getImageUrl()).build()));
        }
        if (file != null && !file.isEmpty()) {
            String newImageUrl = imageService.uploadFiles(List.of(file)).getFirst().getImageUrl();
            member.updateImage(newImageUrl);
        }

        return MemberResDto.of(member);
    }

    public MemberResDto getMyInfo(Long memberId) {
        Member member = getMember(memberId);
        return MemberResDto.of(member);
    }

    public PageApiResponse<PlanThumbResDto> getBookmarkedPlans(int page, int size, Long memberId) {
        Member member = getMember(memberId);

        Pageable pageable = PageRequest.of(page, size);
        Page<PlanThumbResDto> plansDto = bookmarkRepository.findBookmarkedPlansByMember(member, pageable)
                .map(plan -> PlanThumbResDto.of(plan, true));

        return PageApiResponse.of(plansDto);
    }

    public PageApiResponse<PlanThumbResDto> getMyPlans(int page, int size, Long memberId) {
        Member member = getMember(memberId);
        List<Long> bookmarkedIds = bookmarkRepository.findPlanIdsByMember(member);
        Set<Long> bookmarkedSet = new HashSet<>(bookmarkedIds);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<PlanThumbResDto> planDto = planRepository.findByMember(member, pageable)
                .map(plan -> PlanThumbResDto.of(plan, bookmarkedSet.contains(plan.getPlanId())));

        return PageApiResponse.of(planDto);
    }


    public MemberResDto deleteMyInfo(Long memberId) {
        Member member = getMember(memberId);
        bookmarkRepository.deleteByMember(member);
        commentRepository.deleteByMember(member);
        member.withdraw();
        return MemberResDto.of(member);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
