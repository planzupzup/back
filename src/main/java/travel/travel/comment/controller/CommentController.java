package travel.travel.comment.controller;

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
import travel.travel.comment.domain.CommentSortType;
import travel.travel.comment.dto.CommentCreateReqDto;
import travel.travel.comment.dto.CommentResDto;
import travel.travel.comment.dto.CommentUpdateReqDto;
import travel.travel.comment.service.CommentService;
import travel.travel.common.dto.CommonErrorDto;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.dto.PageApiResDto;
import travel.travel.common.service.AuthService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "Comment", description = "댓글 관리 API")
public class CommentController  {

    private final CommentService commentService;
    private final AuthService authService;

    @Operation(summary = "댓글 생성", description = "새로운 댓글을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 생성 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "400", description = "대댓글(2단계)까지만 작성할 수 있습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @PostMapping
    public ResponseEntity<CommonResDto<CommentResDto>> createComment(
            @Parameter(description = "댓글 생성 요청 데이터") @RequestBody CommentCreateReqDto commentCreateReqDto) {
        Long memberId = authService.getAuthenticatedUserId();
        CommentResDto dto = commentService.createComment(commentCreateReqDto, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.CREATED, "댓글저장이 성공적으로 되었습니다.", dto), HttpStatus.CREATED);
    }

    @Operation(summary = "계획별 댓글 목록 조회", description = "특정 계획의 부모 댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 계획을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/{planId}/{type}")
    public ResponseEntity<CommonResDto<PageApiResDto<CommentResDto>>> getComments(
            @Parameter(description = "계획 ID") @PathVariable Long planId,
            @Parameter(description = "정렬 타입 (LATEST: 최신순, POPULAR: 인기순)") @PathVariable CommentSortType type,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {

        PageApiResDto<CommentResDto> dtos = authService.isAuthenticatedUser()
                ? commentService.getCommentsByPost(planId, page, size, type, authService.getAuthenticatedUserId())
                : commentService.getCommentsByPost(planId, page, size, type);

        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "플랜기준 부모 댓글조회가 성공적으로 되었습니다.", dtos), HttpStatus.OK);
    }

    @Operation(summary = "대댓글 목록 조회", description = "특정 댓글의 대댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 목록 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 댓글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/{planId}/{commentId}/{type}")
    public ResponseEntity<CommonResDto<PageApiResDto<CommentResDto>>> getComments(
            @Parameter(description = "계획 ID") @PathVariable Long planId,
            @Parameter(description = "부모 댓글 ID") @PathVariable Long commentId,
            @Parameter(description = "정렬 타입 (LATEST: 최신순, POPULAR: 인기순)") @PathVariable CommentSortType type,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {
        PageApiResDto<CommentResDto> dtos = authService.isAuthenticatedUser()
                ? commentService.getCommentsByParent(planId, commentId, page, size, type, authService.getAuthenticatedUserId())
                : commentService.getCommentsByParent(planId, commentId, page, size, type);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "부모댓글 기준으로 자식댓글 조회가 성공적으로 되었습니다.", dtos), HttpStatus.OK);
    }

    @Operation(summary = "댓글 수정", description = "기존 댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "수정할 권한이 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 댓글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResDto<CommentResDto>> updateComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Parameter(description = "댓글 수정 요청 데이터") @RequestBody CommentUpdateReqDto commentUpdateReqDto) {
        Long memberId = authService.getAuthenticatedUserId();
        CommentResDto dto = commentService.updateComment(commentId,commentUpdateReqDto, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "댓글수정이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "댓글 삭제", description = "기존 댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "삭제할 권한이 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 댓글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonResDto<CommentResDto>> deleteComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        Long memberId = authService.getAuthenticatedUserId();
        commentService.deleteComment(commentId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "댓글삭제가 성공적으로 되었습니다.", null), HttpStatus.OK);
    }
}
