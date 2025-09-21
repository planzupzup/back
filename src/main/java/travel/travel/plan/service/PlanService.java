package travel.travel.plan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import travel.travel.bookmark.repository.BookmarkRepository;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.common.exception.CustomErrorCode;
import travel.travel.common.exception.CustomException;
import travel.travel.location.dto.LocationResDto;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.domain.PlanOwnership;
import travel.travel.plan.domain.PlanSortType;
import travel.travel.plan.dto.*;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.DestinationRepository;
import travel.travel.plan.repository.PlanRepository;


import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PlanService{
    private final PlanRepository planRepository;
    private final DestinationRepository destinationRepository;
    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final AreaCodeService areaCodeService;

    public PlanResDto createPlan(PlanCreateReqDto planCreateReqDto,Long memberId) {
        Member member = getMember(memberId);
        Destination destination = destinationRepository.findByDestinationName(planCreateReqDto.getDestinationName())
                .orElseThrow(() -> new CustomException(CustomErrorCode.DESTINATION_NOT_FOUND));

        if (planCreateReqDto.getStartDate().isAfter(planCreateReqDto.getEndDate())) {
            throw new CustomException(CustomErrorCode.INVALID_DATE_RANGE);
        }

        Long areaCode = areaCodeService.getAreaCodeByName(destination.getDestinationName());
        Plan savedPlan = planRepository.save(
                PlanCreateReqDto.toEntity(planCreateReqDto, member, destination, areaCode)
        );

        return PlanResDto.of(savedPlan, PlanOwnership.MINE, savedPlan.isPublic(), null);
    }

    public PlanResDto getPlanByDay(Long planId, Integer day) {
        Plan existingPlan = findPlan(planId);

        List<LocationResDto> filteredLocations = existingPlan.getLocations().stream()
                .filter(location -> location.getDay().equals(day))
                .map(LocationResDto::of)
                .toList();

        return PlanResDto.of(existingPlan, PlanOwnership.OTHERS, false, filteredLocations);
    }

    public PlanResDto getPlanByDay(Long planId, Integer day, Long memberId) {
        Member member = getMember(memberId);
        Plan existingPlan = findPlan(planId);

        List<LocationResDto> filteredLocations = existingPlan.getLocations().stream()
                .filter(location -> location.getDay().equals(day))
                .map(LocationResDto::of)
                .toList();

        if (member.equals(existingPlan.getMember())) {
            return PlanResDto.of(existingPlan, PlanOwnership.MINE, existingPlan.isPublic(), null);
        }
        boolean bookmarked = isBookmarked(member, existingPlan);
        return PlanResDto.of(existingPlan,  PlanOwnership.OTHERS, bookmarked, filteredLocations);
    }

    public PlanResDto getPlan(Long planId) {
        Plan existingPlan = findPlan(planId);

        List<LocationResDto> filteredLocations = existingPlan.getLocations().stream()
                .map(LocationResDto::of)
                .toList();

        return PlanResDto.of(existingPlan, PlanOwnership.OTHERS, false, filteredLocations);
    }

    public PlanResDto getPlan(Long planId, Long memberId) {
        Member member = getMember(memberId);
        Plan existingPlan = findPlan(planId);

        List<LocationResDto> filteredLocations = existingPlan.getLocations().stream()
                .map(LocationResDto::of)
                .toList();

        if (member.equals(existingPlan.getMember())) {
            return PlanResDto.of(existingPlan, PlanOwnership.MINE, existingPlan.isPublic(), filteredLocations);
        }
        boolean bookmarked = isBookmarked(member, existingPlan);
        return PlanResDto.of(existingPlan, PlanOwnership.OTHERS, bookmarked, filteredLocations);
    }

    public PageApiResponse<PlanThumbResDto> getAllPlan(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<PlanThumbResDto> planDto = planRepository.findAllByIsPublicTrue(pageable)
                .map(plan -> PlanThumbResDto.of(plan, false));

        return PageApiResponse.of(planDto);

    }

    public PageApiResponse<PlanThumbResDto> getAllPlan(int page, int size, Long memberId) {
        Member member = getMember(memberId);
        List<Long> bookmarkedIds = bookmarkRepository.findPlanIdsByMember(member);
        Set<Long> bookmarkedSet = new HashSet<>(bookmarkedIds);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<PlanThumbResDto> planDto = planRepository.findAllByIsPublicTrue(pageable)
                .map(plan -> PlanThumbResDto.of(plan, bookmarkedSet.contains(plan.getPlanId())));

        return PageApiResponse.of(planDto);
    }

    public PageApiResponse<PlanThumbResDto> getAllPlanByKeyword(String keyword, PlanSortType type, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PlanThumbResDto> planDto = switch (type) {
            case COMMENT -> planRepository.searchByKeywordAndOrderByCommentCountAndIsPublicTrue(keyword, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, false));
            case BOOKMARK -> planRepository.searchByKeywordAndOrderByBookmarkCountAndIsPublicTrue(keyword, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, false));
            case LATEST -> planRepository.searchByKeywordAndIsPublicTrue(keyword, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, false));
        };


        return PageApiResponse.of(planDto);
    }

    public PageApiResponse<PlanThumbResDto> getAllPlanByKeyword(String keyword, PlanSortType type, int page, int size, Long memberId) {
        Member member = getMember(memberId);
        List<Long> bookmarkedIds = bookmarkRepository.findPlanIdsByMember(member);
        Set<Long> bookmarkedSet = new HashSet<>(bookmarkedIds);

        Pageable pageable = PageRequest.of(page, size);

        Page<PlanThumbResDto> planDto = switch (type) {
            case COMMENT -> planRepository.searchByKeywordAndOrderByCommentCountAndIsPublicTrue(keyword, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, bookmarkedSet.contains(plan.getPlanId())));
            case BOOKMARK -> planRepository.searchByKeywordAndOrderByBookmarkCountAndIsPublicTrue(keyword, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, bookmarkedSet.contains(plan.getPlanId())));
            case LATEST -> planRepository.searchByKeywordAndIsPublicTrue(keyword, pageable)
                    .map(plan -> PlanThumbResDto.of(plan, bookmarkedSet.contains(plan.getPlanId())));
        };


        return PageApiResponse.of(planDto);
    }

    public PlanResDto updatePlan(Long planId, PlanUpdateReqDto planUpdateReqDto, Long memberId) {
        Member member = getMember(memberId);

        Plan existingPlan = findPlan(planId);
        if (!existingPlan.getMember().equals(member)) {
            throw new CustomException(CustomErrorCode.UPDATE_DENIED);
        }

        existingPlan.updatePlan(
                planUpdateReqDto.isPublic(),planUpdateReqDto.getTitle(),
                planUpdateReqDto.getContent(), planUpdateReqDto.getStartDate(), planUpdateReqDto.getEndDate());

        List<LocationResDto> filteredLocations = existingPlan.getLocations().stream()
                .map(LocationResDto::of)
                .toList();

        Plan savedPlan = planRepository.save(existingPlan);
        return PlanResDto.of(savedPlan, PlanOwnership.MINE, savedPlan.isPublic(), filteredLocations);
    }

    public PlanResDto updatePublicStatus(Long planId, Long memberId) {
        Member member = getMember(memberId);

        Plan findPlan = findPlan(planId);
        if (!findPlan.getMember().equals(member)) {
            throw new CustomException(CustomErrorCode.UPDATE_DENIED);
        }

        findPlan.updatePublic(findPlan.isPublic());
        List<LocationResDto> filteredLocations = findPlan.getLocations().stream()
                .map(LocationResDto::of)
                .toList();

        return PlanResDto.of(findPlan, PlanOwnership.MINE, findPlan.isPublic(), filteredLocations);
    }

    public PlanResDto deletePlan(Long planId, Long memberId) {
        Member member = getMember(memberId);

        Plan existingPlan = findPlan(planId);
        if (!existingPlan.getMember().equals(member)) {
            throw new CustomException(CustomErrorCode.DELETE_DENIED);
        }

        planRepository.delete(existingPlan);
        return PlanResDto.of(existingPlan, PlanOwnership.MINE, false, null);
    }

    private Plan findPlan(Long planId) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));
        return existingPlan;
    }

    private boolean isBookmarked(Member member, Plan savedPlan) {
        return bookmarkRepository.existsByMemberAndPlan(member, savedPlan);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.MEMBER_NOT_FOUND));
    }
}
