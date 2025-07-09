package travel.travel.plan.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.location.domain.Location;
import travel.travel.location.dto.LocationOrderUpdateReqDto;
import travel.travel.location.dto.LocationThumbResDto;
import travel.travel.location.repository.LocationRepository;
import travel.travel.location.service.LocationService;
import travel.travel.member.domain.Member;
import travel.travel.member.repository.MemberRepository;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.dto.PlanThumbResDto;
import travel.travel.plan.dto.PlanUpdateReqDto;
import travel.travel.plan.repository.DestinationRepository;
import travel.travel.plan.repository.PlanRepository;


import java.util.List;
import java.util.Map;
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

    public PlanResDto createPlan(PlanCreateReqDto planCreateReqDto) {
//        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Destination destination = destinationRepository.findByDestinationName(planCreateReqDto.getDestinationName())
                .orElseThrow(()->new EntityNotFoundException("존재하지 않는 장소입니다."));

        if (planCreateReqDto.getStartDate().isAfter(planCreateReqDto.getEndDate())) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }

        Plan savedPlan = planRepository.save(
                PlanCreateReqDto.toEntity(planCreateReqDto, member, destination)
        );
        return PlanResDto.of(savedPlan);
    }


    public PlanResDto getPlanByDay(Long planId, Integer day) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        List<LocationThumbResDto> filteredLocations = existingPlan.getLocations().stream()
                .filter(location -> location.getDay().equals(day))
                .map(LocationThumbResDto::of)
                .toList();

        return PlanResDto.of(existingPlan, filteredLocations);
    }


    public PlanResDto getPlan(Long planId) {
        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        List<LocationThumbResDto> filteredLocations = existingPlan.getLocations().stream()
                .map(LocationThumbResDto::of)
                .toList();

        return PlanResDto.of(existingPlan, filteredLocations);
    }

    public PageApiResponse<PlanThumbResDto> getAllPlan(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<Plan> plans = planRepository.findAll(pageable);
        List<PlanThumbResDto> content = plans.map(PlanThumbResDto::of).getContent();

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

    public PlanResDto updatePlan(Long planId, PlanUpdateReqDto planUpdateReqDto) {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
        existingPlan.updatePlan(planUpdateReqDto.isPublic(),planUpdateReqDto.getTitle(), planUpdateReqDto.getContent(), planUpdateReqDto.getStartDate(), planUpdateReqDto.getEndDate());

        if (!existingPlan.getMember().getId().equals(member.getId())) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        Plan savedPlan = planRepository.save(existingPlan);

        return PlanResDto.of(savedPlan);

    }

    public PlanResDto updateScheduleOrder(Long planId, List<LocationOrderUpdateReqDto> locationOrderUpdateReqDtos) {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));


        Plan existingPlan =  planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        if (!existingPlan.getMember().getId().equals(member.getId())) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        Map<Integer, List<LocationOrderUpdateReqDto>> groupedByDay = locationOrderUpdateReqDtos.stream()
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

        return PlanResDto.of(existingPlan);
    }

    public PlanResDto deletePlan(Long planId) {
        //        String memberId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String memberId = "1";
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Plan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        if (!existingPlan.getMember().getId().equals(member.getId())) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        planRepository.delete(existingPlan);
        return PlanResDto.of(existingPlan);
    }

    public PageApiResponse<PlanThumbResDto> getAllPlanByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdTime"));
        Page<Plan> plans = planRepository.searchByKeyword(keyword, pageable);
        List<PlanThumbResDto> content = plans.map(PlanThumbResDto::of).getContent();

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
}
