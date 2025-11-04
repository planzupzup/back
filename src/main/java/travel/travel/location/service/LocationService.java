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
import travel.travel.location.repository.LocationBulkJdbcRepository;
import travel.travel.location.repository.LocationRepository;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.repository.PlanRepository;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    private final PlanRepository planRepository;
    private final LocationBulkJdbcRepository locationBulkJdbcRepository;

    public void createLocation(List<List<LocationCreateReqDto>> locationCreateReqDtoList, Long planId) {
        Plan findPlan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PLAN_NOT_FOUND));
        if (!findPlan.getLocations().isEmpty()) {
            locationRepository.deleteAllByPlanId(findPlan.getPlanId());
        }

        int day = 1;
        for (int i = 0; i < locationCreateReqDtoList.size(); i++) {
            validateDay(findPlan, day);
            day++;
        }

        locationBulkJdbcRepository.bulkInsertByDay(locationCreateReqDtoList, planId);
    }

    private void validateDay(Plan plan, Integer day) {
        long totalDays = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
        if (day < 1 || day > totalDays) {
            throw new CustomException(CustomErrorCode.INVALID_DATE_RANGE);
        }
    }

    public LocationResDto getLocation(Long locationId) {
        Location location = locationRepository.findByIdWithImages(locationId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.LOCATION_NOT_FOUND));

        return LocationResDto.of(location);
    }

}