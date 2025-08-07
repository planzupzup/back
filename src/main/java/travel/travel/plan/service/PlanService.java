package travel.travel.plan.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import travel.travel.bookmark.repository.BookmarkRepository;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.location.domain.Location;
import travel.travel.location.dto.LocationOrderUpdateReqDto;
import travel.travel.location.dto.LocationResDto;
import travel.travel.location.repository.LocationRepository;
import travel.travel.location.service.LocationService;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.domain.PlanSortType;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.dto.PlanThumbResDto;
import travel.travel.plan.dto.PlanUpdateReqDto;
import travel.travel.plan.repository.DestinationRepository;
import travel.travel.plan.repository.PlanRepository;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PlanService{
    private final PlanRepository planRepository;
    private final DestinationRepository destinationRepository;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;
    private final LocationService locationService;
    private final BookmarkRepository bookmarkRepository;

    public PlanResDto createPlan(PlanCreateReqDto planCreateReqDto,Long memberId) {
        Member member = getMember(memberId);
        Destination destination = destinationRepository.findByDestinationName(planCreateReqDto.getDestinationName())
                .orElseThrow(()->new EntityNotFoundException("존재하지 않는 장소입니다."));

        if (planCreateReqDto.getStartDate().isAfter(planCreateReqDto.getEndDate())) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }

        Plan savedPlan = planRepository.save(
                PlanCreateReqDto.toEntity(planCreateReqDto, member, destination)
        );

        boolean bookmarked = isBookmarked(member, savedPlan);
        return PlanResDto.of(savedPlan, bookmarked, null);
    }

    public PlanResDto getPlanByDay(Long planId, Integer day) {
        return buildPlanResDto(planId, day, null);
    }

    public PlanResDto getPlanByDay(Long planId, Integer day, Long memberId) {
        return buildPlanResDto(planId, day, memberId);
    }

    private PlanResDto buildPlanResDto(Long planId, Integer day, Long memberId) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        List<LocationResDto> filteredLocations = existingPlan.getLocations().stream()
                .filter(location -> location.getDay().equals(day))
                .map(LocationResDto::of)
                .toList();

        boolean bookmarked = false;
        if (memberId != null) {
            Member member = getMember(memberId);
            bookmarked = isBookmarked(member, existingPlan);
        }

        return PlanResDto.of(existingPlan, bookmarked, filteredLocations);
    }

    public PlanResDto getPlan(Long planId) {
        return buildPlanResDto(planId, null);
    }

    public PlanResDto getPlan(Long planId, Long memberId) {
        return buildPlanResDto(planId, memberId);
    }


    public PlanResDto buildPlanResDto(Long planId, Long memberId) {

        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        List<LocationResDto> filteredLocations = existingPlan.getLocations().stream()
                .map(LocationResDto::of)
                .toList();

        boolean bookmarked = false;
        if (memberId != null) {
            Member member = getMember(memberId);
            bookmarked = isBookmarked(member, existingPlan);
        }

        return PlanResDto.of(existingPlan, bookmarked, filteredLocations);
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

        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        if (!existingPlan.getMember().getId().equals(member.getId())) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        existingPlan.updatePlan(
                planUpdateReqDto.isPublic(),planUpdateReqDto.getTitle(),
                planUpdateReqDto.getContent(), planUpdateReqDto.getStartDate(), planUpdateReqDto.getEndDate());

        List<LocationResDto> filteredLocations = existingPlan.getLocations().stream()
                .map(LocationResDto::of)
                .toList();

        Plan savedPlan = planRepository.save(existingPlan);
        boolean bookmark = isBookmarked(member, existingPlan);
        return PlanResDto.of(savedPlan, bookmark, filteredLocations);
    }

    public PlanResDto updatePublicStatus(Long planId, Long memberId) {
        Member member = getMember(memberId);
        Plan findPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        if (!findPlan.getMember().getId().equals(memberId)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        findPlan.updatePublic(findPlan.isPublic());

        List<LocationResDto> filteredLocations = findPlan.getLocations().stream()
                .map(LocationResDto::of)
                .toList();
        boolean bookmark = isBookmarked(member, findPlan);
        return PlanResDto.of(findPlan, bookmark, filteredLocations);
    }

    public PlanResDto updateScheduleOrder(Long planId, List<LocationOrderUpdateReqDto> locationOrderUpdateReqDto, Long memberId) {
        Member member = getMember(memberId);

        Plan existingPlan =  planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        if (!existingPlan.getMember().getId().equals(member.getId())) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        Map<Integer, List<LocationOrderUpdateReqDto>> groupedByDay = locationOrderUpdateReqDto.stream()
                .collect(Collectors.groupingBy(LocationOrderUpdateReqDto::getDay));

        for (Map.Entry<Integer, List<LocationOrderUpdateReqDto>> entry : groupedByDay.entrySet()) {
            Integer day = entry.getKey();

            for (LocationOrderUpdateReqDto dto : entry.getValue()) {
                Location location = locationRepository.findById(dto.getLocationId())
                        .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다."));

                if (!location.getPlan().getPlanId().equals(planId)) {
                    throw new IllegalArgumentException("요청 정보와 일치하지 않는 지역입니다.");
                }

                location.updateScheduleOrder(dto.getScheduleOrder());
                location.updateDay(dto.getDay());
            }

            List<Location> reordered = locationRepository.findByPlanAndDayOrderByScheduleOrderAsc(existingPlan, day);
            locationService.checkForDuplicateScheduleOrder(reordered);
            locationService.autoScheduleOrder(reordered);
        }

        List<LocationResDto> filteredLocations = existingPlan.getLocations().stream()
                .map(LocationResDto::of)
                .toList();

        boolean bookmark = isBookmarked(member, existingPlan);
        return PlanResDto.of(existingPlan, bookmark, filteredLocations);
    }

    public PlanResDto deletePlan(Long planId, Long memberId) {
        Member member = getMember(memberId);

        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        if (!existingPlan.getMember().getId().equals(member.getId())) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        planRepository.delete(existingPlan);
        return PlanResDto.of(existingPlan, false, null);
    }


    private boolean isBookmarked(Member member, Plan savedPlan) {
        return bookmarkRepository.existsByMemberAndPlan(member, savedPlan);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
    }
}
