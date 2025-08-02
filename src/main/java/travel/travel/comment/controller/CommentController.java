package travel.travel.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.comments.CommentType;
import travel.travel.comment.domain.CommentSortType;
import travel.travel.comment.dto.CommentCreateReqDto;
import travel.travel.comment.dto.CommentResDto;
import travel.travel.comment.dto.CommentUpdateReqDto;
import travel.travel.comment.service.CommentService;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.common.service.AuthService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController  {

    private final CommentService commentService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<CommonResDto<CommentResDto>> createComment(@RequestBody CommentCreateReqDto commentCreateReqDto) {
        Long memberId = authService.getAuthenticatedUserId();
        CommentResDto dto = commentService.createComment(commentCreateReqDto, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.CREATED, "댓글저장이 성공적으로 되었습니다.", dto), HttpStatus.CREATED);
    }

    @GetMapping("/{planId}/{type}")
    public ResponseEntity<CommonResDto<PageApiResponse<CommentResDto>>> getComments(
            @PathVariable Long planId,
            @PathVariable CommentSortType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageApiResponse<CommentResDto> dtos = authService.isAuthenticatedUser()
                ? commentService.getCommentsByPost(planId, page, size, type, authService.getAuthenticatedUserId())
                : commentService.getCommentsByPost(planId, page, size, type);

        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "플랜기준 부모 댓글조회가 성공적으로 되었습니다.", dtos), HttpStatus.OK);
    }

    @GetMapping("/{planId}/{commentId}/{type}")
    public ResponseEntity<CommonResDto<PageApiResponse<CommentResDto>>> getComments(
            @PathVariable Long planId, @PathVariable Long commentId, @PathVariable CommentSortType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageApiResponse<CommentResDto> dtos = authService.isAuthenticatedUser()
                ? commentService.getCommentsByParent(planId, commentId, page, size, type, authService.getAuthenticatedUserId())
                : commentService.getCommentsByParent(planId, commentId, page, size, type);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "부모댓글 기준으로 자식댓글 조회가 성공적으로 되었습니다.", dtos), HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResDto<CommentResDto>> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateReqDto commentUpdateReqDto) {
        Long memberId = authService.getAuthenticatedUserId();
        CommentResDto dto = commentService.updateComment(commentId,commentUpdateReqDto, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "댓글수정이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonResDto<CommentResDto>> deleteComment(@PathVariable Long commentId) {
        Long memberId = authService.getAuthenticatedUserId();
        commentService.deleteComment(commentId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "댓글삭제가 성공적으로 되었습니다.", null), HttpStatus.OK);
    }
}
