package travel.travel.mypage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;
import travel.travel.bookmark.repository.BookmarkRepository;
import travel.travel.comment.repository.CommentRepository;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.common.exception.CustomErrorCode;
import travel.travel.common.exception.CustomException;
import travel.travel.image.domain.Image;
import travel.travel.image.repository.ImageRepository;
import travel.travel.image.service.ImageService;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.PlanSortType;
import travel.travel.plan.dto.PlanThumbResDto;
import travel.travel.plan.repository.PlanRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ImageService imageService;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;

    public MemberResDto updateMyInfo(MemberReqDto memberReqDto, MultipartFile file, Long memberId) {
        Member member = getMember(memberId);
        member.updateInfo(memberReqDto.getNickName(), memberReqDto.getDescription());

        if (member.getImageUrl() != null) {
            Image image = imageRepository.findByImageUrl(member.getImageUrl())
                    .orElseThrow(() -> new CustomException(CustomErrorCode.IMAGE_NOT_FOUND));

            imageService.deleteImages(List.of(image));
            member.updateImage(null);
        }
        if (file != null && !file.isEmpty()) {
            try {
                String newImageUrl = imageService.uploadFiles(List.of(file)).getFirst().getImageUrl();
                member.updateImage(newImageUrl);
                log.info("이미지 업데이트 성공 : {}", newImageUrl);
            } catch (S3Exception e) {
                log.error("S3 이미지 업로드 실패: {}", e.getMessage(), e);
                throw new CustomException(CustomErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }

        return MemberResDto.of(member);
    }

    public MemberResDto getMyInfo(Long memberId) {
        Member member = getMember(memberId);
        return MemberResDto.of(member);
    }

    public PageApiResponse<PlanThumbResDto> getBookmarkedPlans(PlanSortType type, int page, int size, Long memberId) {
        Member member = getMember(memberId);

        Pageable pageable = PageRequest.of(page, size);

        Page<PlanThumbResDto> plansDto = switch (type) {
            case COMMENT -> bookmarkRepository.findBookmarkedPlansByMemberOrderByCommentCountAndIsPublicTrue(member, pageable)
                .map(plan -> PlanThumbResDto.of(plan, true));
            case BOOKMARK -> bookmarkRepository.findBookmarkedPlansByMemberOrderByBookmarkCountAndIsPublicTrue(member, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, true));
            case LATEST -> bookmarkRepository.findBookmarkedPlansByMemberAndIsPublicTrue(member,pageable)
                    .map(plan -> PlanThumbResDto.of(plan, true));
        };

        return PageApiResponse.of(plansDto);
    }

    public PageApiResponse<PlanThumbResDto> getMyPlans(VisibilityType visibility, int page, int size, Long memberId) {
        Member member = getMember(memberId);
        List<Long> bookmarkedIds = bookmarkRepository.findPlanIdsByMember(member);
        Set<Long> bookmarkedSet = new HashSet<>(bookmarkedIds);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        planRepository.findByMember(member, pageable)
                .map(plan -> PlanThumbResDto.of(plan, bookmarkedSet.contains(plan.getPlanId())));

        Page<PlanThumbResDto> planDto = switch (visibility) {
            case PUBLIC -> planRepository.findByIsPublicAndMember(true, member, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, bookmarkedSet.contains(plan.getPlanId())));
            case PRIVATE -> planRepository.findByIsPublicAndMember(false, member, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, bookmarkedSet.contains(plan.getPlanId())));
            case ALL -> planRepository.findByMember(member, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, bookmarkedSet.contains(plan.getPlanId())));
        };

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
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));
    }
}
