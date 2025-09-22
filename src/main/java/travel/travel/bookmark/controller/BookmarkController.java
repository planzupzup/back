package travel.travel.bookmark.controller;

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
import org.springframework.web.bind.annotation.*;
import travel.travel.bookmark.service.BookmarkService;
import travel.travel.common.dto.CommonErrorDto;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.service.AuthService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/plan")
@Tag(name = "Bookmark", description = "북마크 관리 API")
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    private final AuthService authService;

    @Operation(summary = "북마크 추가", description = "특정 계획에 북마크를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 추가 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "이미 눌렀습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @PostMapping("/{planId}/bookmark")
    public ResponseEntity<CommonResDto<Long>> addBookmark(
            @Parameter(description = "계획 ID") @PathVariable Long planId) {
        Long memberId = authService.getAuthenticatedUserId();
        bookmarkService.addBookmark(planId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "북마크추가가 성공적으로 되었습니다.",planId ), HttpStatus.OK);
    }

    @Operation(summary = "북마크 취소", description = "특정 계획의 북마크를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 취소 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @DeleteMapping("/{planId}/bookmark")
    public ResponseEntity<CommonResDto<Long>> removeBookmark(
            @Parameter(description = "계획 ID") @PathVariable Long planId) {
        Long memberId = authService.getAuthenticatedUserId();
        bookmarkService.removeBookmark(planId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "북마크취소가 성공적으로 되었습니다.", planId), HttpStatus.OK);
    }

    @Operation(summary = "북마크 개수 조회", description = "특정 계획의 북마크 개수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크 개수 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/{planId}/bookmark")
    public ResponseEntity<CommonResDto<Long>> getBookmarkCount(
            @Parameter(description = "계획 ID") @PathVariable Long planId) {
        Long bookmarkCount = bookmarkService.getBookmarkCount(planId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "북마크개수조회가 성공적으로 되었습니다.", bookmarkCount), HttpStatus.OK);
    }
}
