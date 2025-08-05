package travel.travel.plan.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.common.service.AuthService;
import travel.travel.location.dto.LocationOrderUpdateReqDto;
import travel.travel.plan.domain.PlanSortType;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.dto.PlanThumbResDto;
import travel.travel.plan.dto.PlanUpdateReqDto;
import travel.travel.plan.service.PlanService;

import java.time.Duration;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanService planService;
    private final AuthService authService;
  
    private final Bucket bucket = Bucket4j.builder()
            .addLimit(Bandwidth.simple(5, Duration.ofSeconds(1)))
            .build();


    @PostMapping
    public ResponseEntity<CommonResDto<PlanResDto>> createPlan(@Valid @RequestBody PlanCreateReqDto planCreateReqDto) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                 .body(CommonResDto.of(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.", null));
        }
      
        Long memberId = authService.getAuthenticatedUserId();
        PlanResDto dto = planService.createPlan(planCreateReqDto, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.CREATED, "계획생성이 성공적으로 되었습니다.", dto), HttpStatus.CREATED);
    }

    @GetMapping("/{planId}/{day}")
    public ResponseEntity<CommonResDto<PlanResDto>> getPlanByDay(@PathVariable Long planId, @PathVariable Integer day) {

        PlanResDto dto = authService.isAuthenticatedUser()
                ? planService.getPlanByDay(planId, day, authService.getAuthenticatedUserId())
                : planService.getPlanByDay(planId, day);

        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "날짜별 지역목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<CommonResDto<PlanResDto>> getPlan(@PathVariable Long planId) {

        PlanResDto dto = authService.isAuthenticatedUser()
                ? planService.getPlan(planId, authService.getAuthenticatedUserId())
                : planService.getPlan(planId);

        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획상세조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CommonResDto<PageApiResponse<PlanThumbResDto>>> getAllPlan(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {

        PageApiResponse<PlanThumbResDto> dto = authService.isAuthenticatedUser()
                ? planService.getAllPlan(page, size, authService.getAuthenticatedUserId())
                : planService.getAllPlan(page, size);

        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }


    @GetMapping("/search/{keyword}/{type}")
    public ResponseEntity<CommonResDto<PageApiResponse<PlanThumbResDto>>> getAllPlanByKeyword(
            @PathVariable String keyword,
            @PathVariable PlanSortType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageApiResponse<PlanThumbResDto> dto = authService.isAuthenticatedUser()
                ? planService.getAllPlanByKeyword(keyword, type, page, size, authService.getAuthenticatedUserId())
                : planService.getAllPlanByKeyword(keyword, type, page, size);

        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @PutMapping("/{planId}/order")
    public ResponseEntity<CommonResDto<PlanResDto>> updateScheduleOrder(
            @PathVariable Long planId,
            @RequestBody List<LocationOrderUpdateReqDto> locationOrderUpdateReqDtos) {
        Long memberId = authService.getAuthenticatedUserId();
        PlanResDto dto = planService.updateScheduleOrder(planId, locationOrderUpdateReqDtos, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "지역날짜, 순서 변경이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @PutMapping("/{planId}")
    public ResponseEntity<CommonResDto<PlanResDto>> updatePlan(@PathVariable Long planId, @Valid @RequestBody PlanUpdateReqDto planUpdateReqDto) {
        Long memberId = authService.getAuthenticatedUserId();
        PlanResDto dto = planService.updatePlan(planId, planUpdateReqDto, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획수정이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<CommonResDto<PlanResDto>> deletePlan(@PathVariable Long planId) {
        Long memberId = authService.getAuthenticatedUserId();
        PlanResDto dto = planService.deletePlan(planId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획삭제가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

}
