package travel.travel.plan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.travel.common.dto.CommonErrorDto;
import travel.travel.common.dto.CommonResDto;
import travel.travel.plan.dto.DestinationResDto;
import travel.travel.plan.service.DestinationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/destination")
@Tag(name = "Destination", description = "목적지 관리 API")
public class DestinationController {

    private final DestinationService destinationService;

    @Operation(summary = "목적지 검색", description = "장소명 또는 국가명으로 목적지를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목적지 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 장소를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/{map}")
    public ResponseEntity<CommonResDto<List<DestinationResDto>>> findDestination(
            @Parameter(description = "장소명 또는 국가명") @PathVariable String map) {
        List<DestinationResDto> dto = destinationService.findDestination(map);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "목적지 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }
}
