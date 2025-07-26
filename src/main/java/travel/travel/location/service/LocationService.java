package travel.travel.location.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.image.domain.Image;
import travel.travel.image.dto.ImageResDto;
import travel.travel.image.repository.ImageRepository;
import travel.travel.image.service.ImageService;
import travel.travel.location.domain.Location;
import travel.travel.location.dto.LocationCreateReqDto;
import travel.travel.location.dto.LocationResDto;
import travel.travel.location.dto.LocationUpdateReqDto;
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
    private final ImageService imageService;
    private final ImageRepository imageRepository;

//    public LocationResDto createLocation(LocationCreateReqDto locationCreateReqDto, List<MultipartFile> files) {
//        Plan plan = planRepository.findById(locationCreateReqDto.getPlanId())
//                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
//
//        long total = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
//        if (locationCreateReqDto.getDay() < 1 || locationCreateReqDto.getDay() > total) {
//            throw new IllegalArgumentException("요청하신 day 값이 계획 범위를 벗어났습니다.");
//        }
//
//        List<Image> images = new ArrayList<>();
//        if (files != null && !files.isEmpty()) {
//            List<ImageResDto> imageResDtos = imageService.uploadFiles(files);
//            images = imageResDtos.stream()
//                    .map(img -> imageRepository.findById(img.getImageId())
//                            .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다.")))
//                    .toList();
//        }
//
//        Location findLocation = locationRepository.findTopByPlanAndDayOrderByScheduleOrderDesc(plan, locationCreateReqDto.getDay());
//        int lastOrderNumber = (findLocation != null) ? findLocation.getScheduleOrder() : 0;
//        int newOrderNumber = lastOrderNumber + 1;
//
//        Location savedLocation = locationRepository.save(
//                LocationCreateReqDto.toEntity(locationCreateReqDto, plan, images, newOrderNumber));
//
//        return LocationResDto.of(savedLocation);
//    }

    public List<List<LocationResDto>> createLocation(List<List<LocationCreateReqDto>> locationCreateReqDtoList, Long planId) {
        Plan findPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));
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
            throw new IllegalArgumentException("요청하신 day 값이 계획 범위를 벗어났습니다.");
        }
    }

    public LocationResDto getLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다."));

        return LocationResDto.of(location);
    }

    public LocationResDto updateLocation(Long locationId, LocationUpdateReqDto locationUpdateReqDto, List<MultipartFile> files) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 지역이 존재하지 않습니다."));

        imageRepository.deleteAll(location.getImages());

        List<Image> images = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            List<ImageResDto> imageResDtos = imageService.uploadFiles(files);
            images = imageResDtos.stream()
                    .map(img -> imageRepository.findById(img.getImageId())
                            .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다.")))
                    .toList();
        }

        location.updateInfo(
                locationUpdateReqDto.getLocationName(),
                locationUpdateReqDto.getLatitude(),
                locationUpdateReqDto.getLongitude(),
                locationUpdateReqDto.getDescription(),
                locationUpdateReqDto.getGoogleImageUrl(),
                locationUpdateReqDto.getRating(),
                locationUpdateReqDto.getTypes(),
                locationUpdateReqDto.getPlaceId(),
                images
        );
        return LocationResDto.of(location);
    }

    public LocationResDto deleteLocation(Long locationId) {
        Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 위치입니다."));
        Integer day = existingLocation.getDay();
        imageService.deleteImages(existingLocation.getImages());
        locationRepository.delete(existingLocation);

        Plan plan =  planRepository.findById(existingLocation.getPlan().getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        autoScheduleOrder(locationRepository.findByPlanAndDayOrderByScheduleOrderAsc(plan, day));

        return LocationResDto.of(existingLocation);
    }

    public void checkForDuplicateScheduleOrder(List<Location> locations) {
        List<Integer> orderList = locations.stream()
                .map(Location::getScheduleOrder).toList();

        long distinctCount = orderList.stream().distinct().count();
        if (distinctCount != orderList.size()) {
            throw new IllegalArgumentException("중복된 scheduleOrder 값이 있습니다. 순서를 다시 확인해주세요.");
        }
    }

    public void autoScheduleOrder(List<Location> locations) {
        int newOrderNumber = 1;
        for (Location location : locations) {
            location.updateScheduleOrder(newOrderNumber++);
            locationRepository.save(location);
        }
    }
}