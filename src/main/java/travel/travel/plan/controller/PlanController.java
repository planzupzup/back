package travel.travel.plan.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
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
import travel.travel.common.dto.PageApiResDto;
import travel.travel.common.exception.CustomErrorCode;
import travel.travel.common.exception.CustomException;
import travel.travel.common.service.AuthService;
import travel.travel.plan.domain.PlanSortType;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.dto.PlanThumbResDto;
import travel.travel.plan.dto.PlanUpdateReqDto;
import travel.travel.plan.service.PlanService;

import java.time.Duration;

import static travel.travel.plan.domain.PlanSortType.LATEST;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/plan")
@Tag(name = "Plan", description = "여행 계획 관리 API")
public class PlanController {

    private final PlanService planService;
    private final AuthService authService;
  
    private final Bucket bucket = Bucket4j.builder()
            .addLimit(Bandwidth.simple(5, Duration.ofSeconds(1)))
            .build();


    @Operation(summary = "여행 계획 생성", description = "새로운 여행 계획을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "계획 생성 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "400", description = "시작일은 종료일보다 이전이어야 합니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 장소를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "429", description = "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @PostMapping
    public ResponseEntity<CommonResDto<PlanResDto>> createPlan(@Valid @RequestBody PlanCreateReqDto planCreateReqDto) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            throw new CustomException(CustomErrorCode.TOO_MANY_REQUESTS);
        }

        Long memberId = authService.getAuthenticatedUserId();
        PlanResDto dto = planService.createPlan(planCreateReqDto, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.CREATED, "계획생성이 성공적으로 되었습니다.", dto), HttpStatus.CREATED);
    }

    @Operation(summary = "날짜별 계획 조회", description = "특정 계획의 특정 날짜에 해당하는 장소 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "날짜별 장소 목록 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/{planId}/{day}")
    public ResponseEntity<CommonResDto<PlanResDto>> getPlanByDay(
            @Parameter(description = "계획 ID") @PathVariable Long planId,
            @Parameter(description = "조회할 날짜") @PathVariable Integer day) {

        PlanResDto dto = authService.isAuthenticatedUser()
                ? planService.getPlanByDay(planId, day, authService.getAuthenticatedUserId())
                : planService.getPlanByDay(planId, day);

        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "날짜별 장소목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "계획 상세 조회", description = "특정 계획의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계획 상세 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/{planId}")
    public ResponseEntity<CommonResDto<PlanResDto>> getPlan(
            @Parameter(description = "계획 ID") @PathVariable Long planId) {

        PlanResDto dto = authService.isAuthenticatedUser()
                ? planService.getPlan(planId, authService.getAuthenticatedUserId())
                : planService.getPlan(planId);

        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획상세조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "모든 계획 목록 조회", description = "공개된 모든 계획의 목록을 페이지네이션으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "계획 목록 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class)))
    @GetMapping
    public ResponseEntity<CommonResDto<PageApiResDto<PlanThumbResDto>>> getAllPlan(
            @Parameter(description = "정렬 타입")   @RequestParam(defaultValue = "LATEST") PlanSortType type,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
            ) {

        PageApiResDto<PlanThumbResDto> dto = authService.isAuthenticatedUser()
                ? planService.getAllPlan(type, page, size, authService.getAuthenticatedUserId())
                : planService.getAllPlan(type, page, size);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }


    @Operation(summary = "키워드로 계획 검색", description = "키워드와 정렬 타입으로 계획을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "계획 검색 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class)))
    @GetMapping("/search/{keyword}/{type}")
    public ResponseEntity<CommonResDto<PageApiResDto<PlanThumbResDto>>> getAllPlanByKeyword(
            @Parameter(description = "검색 키워드") @PathVariable String keyword,
            @Parameter(description = "정렬 타입") @PathVariable PlanSortType type,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {

        PageApiResDto<PlanThumbResDto> dto = authService.isAuthenticatedUser()
                ? planService.getAllPlanByKeyword(keyword, type, page, size, authService.getAuthenticatedUserId())
                : planService.getAllPlanByKeyword(keyword, type, page, size);

        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "계획 수정", description = "기존 계획을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계획 수정 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "수정할 권한이 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @PutMapping("/{planId}")
    public ResponseEntity<CommonResDto<PlanResDto>> updatePlan(
            @Parameter(description = "계획 ID") @PathVariable Long planId,
            @Valid @RequestBody PlanUpdateReqDto planUpdateReqDto) {
        Long memberId = authService.getAuthenticatedUserId();
        PlanResDto dto = planService.updatePlan(planId, planUpdateReqDto, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획수정이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "계획 공개/비공개 상태 변경", description = "계획의 공개/비공개 상태를 토글합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공개/비공개 상태 변경 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "수정할 권한이 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @PutMapping("/{planId}/public")
    public ResponseEntity<CommonResDto<PlanResDto>> updatePlanPublic(
            @Parameter(description = "계획 ID") @PathVariable Long planId) {
        Long memberId = authService.getAuthenticatedUserId();
        PlanResDto dto = planService.updatePublicStatus(planId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획공개/비공개 수정이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "계획 삭제", description = "기존 계획을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계획 삭제 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "삭제할 권한이 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @DeleteMapping("/{planId}")
    public ResponseEntity<CommonResDto<PlanResDto>> deletePlan(
            @Parameter(description = "계획 ID") @PathVariable Long planId) {
        Long memberId = authService.getAuthenticatedUserId();
        planService.deletePlan(planId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획삭제가 성공적으로 되었습니다.", null), HttpStatus.OK);
    }

}
