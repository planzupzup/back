package travel.travel.plan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.travel.common.dto.CommonResDto;
import travel.travel.plan.dto.DestinationResDto;
import travel.travel.plan.service.DestinationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/destination")
public class DestinationController {

    private final DestinationService destinationService;

    @GetMapping("/{map}")
    public ResponseEntity<CommonResDto<List<DestinationResDto>>> findDestination(@PathVariable String map) {
        List<DestinationResDto> dto = destinationService.findDestination(map);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "목적지 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }
}
