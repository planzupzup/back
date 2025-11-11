package travel.travel.location.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.common.dto.CommonErrorDto;
import travel.travel.common.dto.CommonResDto;
import travel.travel.location.dto.LocationCreateReqDto;
import travel.travel.location.dto.LocationResDto;
import travel.travel.location.service.LocationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/location")
@Tag(name = "Location", description = "장소 관리 API")
public class LocationController {

    private final LocationService locationService;


    @Operation(summary = "장소 생성", description = "특정 계획에 대한 여행 장소들을 날짜별로 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "장소 생성 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "400", description = "시작일과 종료일 사이어야 합니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @PostMapping("/{planId}")
    public ResponseEntity<CommonResDto<Void>> createLocation(
            @Parameter(description = "날짜별 장소 데이터 (2차원 리스트: 날짜 > 장소)") @Valid @RequestBody List<List<LocationCreateReqDto>> locationCreateReqDto,
            @Parameter(description = "계획 ID") @PathVariable Long planId) {
        locationService.createLocation(locationCreateReqDto, planId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.CREATED, "장소저장이 성공적으로 되었습니다.", null), HttpStatus.CREATED);
    }

    @Operation(summary = "장소 상세 조회", description = "특정 장소의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장소 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 장소을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/{locationId}")
    public ResponseEntity<CommonResDto<LocationResDto>> getLocation(
            @Parameter(description = "장소 ID") @PathVariable Long locationId) {
        LocationResDto dto = locationService.getLocation(locationId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "장소상세조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<CommonResDto<LocationResDto>> updateLocation(
            @PathVariable Long locationId,
            @Valid @RequestPart LocationCreateReqDto locationUpdateReqDto) {
        LocationResDto dto = locationService.updateLocation(locationId, locationUpdateReqDto);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "지역변경이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

}