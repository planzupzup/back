package travel.travel.plan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.common.dto.CommonResDto;
import travel.travel.location.dto.LocationOrderUpdateReqDto;
import travel.travel.plan.dto.PlanCreateReqDto;
import travel.travel.plan.dto.PlanResDto;
import travel.travel.plan.dto.PlanThumbResDto;
import travel.travel.plan.dto.PlanUpdateReqDto;
import travel.travel.plan.service.PlanService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/plan")
public class PlanController {
    private final PlanService planService;

    @PostMapping
    public ResponseEntity<CommonResDto> createPlan(@Valid @RequestBody PlanCreateReqDto planCreateReqDto) {
        PlanResDto dto = planService.createPlan(planCreateReqDto);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED, "계획생성이 성공적으로 되었습니다.", dto), HttpStatus.CREATED);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<CommonResDto> getPlan(@PathVariable Long planId) {
        PlanResDto dto = planService.getPlan(planId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "계획상세조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping("/{planId}/{day}")
    public ResponseEntity<CommonResDto> getPlanByDay(@PathVariable Long planId, @PathVariable Integer day) {
        PlanResDto dto = planService.getPlanByDay(planId, day);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "날짜별 지역목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CommonResDto> getAllPlan(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "10") int size) {

        Slice<PlanThumbResDto> dto = planService.getAllPlan(cursor, size);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "계획목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }


    @PutMapping("/{planId}/order")
    public ResponseEntity<CommonResDto> updateScheduleOrder(
            @PathVariable Long planId,
            @RequestBody List<LocationOrderUpdateReqDto> locationOrderUpdateReqDtos) {

        PlanResDto dto = planService.updateScheduleOrder(planId, locationOrderUpdateReqDtos);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "지역날짜, 순서 변경이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @PutMapping("/{planId}")
    public ResponseEntity<CommonResDto> updatePlan(@PathVariable Long planId, @Valid @RequestBody PlanUpdateReqDto planUpdateReqDto) {
        PlanResDto dto = planService.updatePlan(planId, planUpdateReqDto);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "계획수정이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<CommonResDto> deletePlan(@PathVariable Long planId) {
        PlanResDto dto = planService.deletePlan(planId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "계획삭제가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<CommonResDto> getAllPlanByKeyword(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PlanThumbResDto> dto = planService.getAllPlanByKeyword(keyword, page, size);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "계획목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }
}
