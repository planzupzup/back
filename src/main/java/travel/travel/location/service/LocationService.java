package travel.travel.location.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import travel.travel.common.exception.CustomErrorCode;
import travel.travel.common.exception.CustomException;
import travel.travel.location.domain.Location;
import travel.travel.location.dto.LocationCreateReqDto;
import travel.travel.location.dto.LocationResDto;
import travel.travel.location.repository.LocationRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.PlanRepository;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    private final PlanRepository planRepository;


    public List<List<LocationResDto>> createLocation(List<List<LocationCreateReqDto>> locationCreateReqDtoList, Long planId) {
        Plan findPlan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));
        locationRepository.deleteLocationsByPlan(findPlan);
        List<List<LocationResDto>> result = new ArrayList<>();

        Integer day = 1;
        for (List<LocationCreateReqDto> dayLocationList : locationCreateReqDtoList) {
            List<LocationResDto> dayResult = new ArrayList<>();
            int order = 0;
            for (LocationCreateReqDto locationDto : dayLocationList) {

                validateDay(findPlan, day);
                Location saved = locationRepository.save(
                        LocationCreateReqDto.toEntity(locationDto, findPlan, day,order + 1));

                dayResult.add(LocationResDto.of(saved));
                order++;
            }
            day ++;
            result.add(dayResult);
        }
        return result;
    }

    private void validateDay(Plan plan, Integer day) {
        long totalDays = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
        if (day < 1 || day > totalDays) {
            throw new CustomException(CustomErrorCode.INVALID_DATE_RANGE);
        }
    }

    public LocationResDto getLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.LOCATION_NOT_FOUND));

        return LocationResDto.of(location);
    }

}