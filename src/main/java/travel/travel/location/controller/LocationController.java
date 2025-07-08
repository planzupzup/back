package travel.travel.location.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.common.dto.CommonResDto;
import travel.travel.location.dto.LocationCreateReqDto;
import travel.travel.location.dto.LocationResDto;
import travel.travel.location.dto.LocationUpdateReqDto;
import travel.travel.location.service.LocationService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/location")
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<CommonResDto<LocationResDto>> createLocation(
            @Valid @RequestPart LocationCreateReqDto locationCreateReqDto,
            @RequestPart(required = false) List<MultipartFile> files) {
        LocationResDto dto = locationService.createLocation(locationCreateReqDto, files);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.CREATED, "지역저장이 성공적으로 되었습니다.", dto), HttpStatus.CREATED);
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<CommonResDto<LocationResDto>> getLocation(@PathVariable Long locationId) {
        LocationResDto dto = locationService.getLocation(locationId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "지역상세조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<CommonResDto<LocationResDto>> updateLocation(
            @PathVariable Long locationId,
            @Valid @RequestPart LocationUpdateReqDto locationUpdateReqDto,
            @RequestPart(required = false) List<MultipartFile> files) {
        LocationResDto dto = locationService.updateLocation(locationId, locationUpdateReqDto, files);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "지역변경이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<CommonResDto<LocationResDto>> deleteLocation(@PathVariable Long locationId) {
        LocationResDto dto = locationService.deleteLocation(locationId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "지역삭제가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

}