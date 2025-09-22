package travel.travel.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.common.dto.CommonErrorDto;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.service.AuthService;
import travel.travel.like.service.LikeService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/comment")
@Tag(name = "Like", description = "댓글 좋아요 관리 API")
public class LikeController {

    private final LikeService likeService;
    private final AuthService authService;

    @Operation(summary = "좋아요 추가", description = "특정 댓글에 좋아요를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 추가 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 댓글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "409", description = "이미 눌렀습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @PostMapping("/{commentId}/like")
    public ResponseEntity<CommonResDto<Long>> addLike(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        Long memberId = authService.getAuthenticatedUserId();
        likeService.addLike(commentId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "좋아요가 성공적으로 되었습니다.", commentId), HttpStatus.OK);
    }

    @Operation(summary = "좋아요 취소", description = "특정 댓글의 좋아요를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 댓글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<CommonResDto<Long>> removeLike(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        Long memberId = authService.getAuthenticatedUserId();
        likeService.removeLike(commentId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "좋아요취소가 성공적으로 되었습니다.", commentId), HttpStatus.OK);
    }

    @Operation(summary = "좋아요 개수 조회", description = "특정 댓글의 좋아요 개수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 개수 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 댓글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/{commentId}/like")
    public ResponseEntity<CommonResDto<Long>> getLikeCount(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        Long likeCount = likeService.getLikeCount(commentId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "좋아요개수조회가 성공적으로 되었습니다.", likeCount), HttpStatus.OK);
    }
}