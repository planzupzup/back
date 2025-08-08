package travel.travel.plan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.common.dto.CommonResDto;
import travel.travel.plan.dto.PlaceDto;
import travel.travel.plan.service.GooglePlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
public class PlaceController {

    private final GooglePlaceService googlePlaceService;


    @GetMapping("/{planId}")
    public ResponseEntity<CommonResDto<List<PlaceDto>>> searchPlaces(@PathVariable Long planId, @RequestParam String keyword) {
        List<PlaceDto> dto = googlePlaceService.searchPlaces(planId, keyword);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "검색 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }
}

