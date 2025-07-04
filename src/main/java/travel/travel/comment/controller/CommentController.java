package travel.travel.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.comment.dto.CommentCreateReqDto;
import travel.travel.comment.dto.CommentResDto;
import travel.travel.comment.dto.CommentUpdateReqDto;
import travel.travel.comment.service.CommentService;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.dto.PageApiResponse;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController  {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommonResDto> createComment(@RequestBody CommentCreateReqDto commentCreateReqDto) {
        CommentResDto dto = commentService.createComment(commentCreateReqDto);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED, "댓글저장이 성공적으로 되었습니다.", dto), HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResDto> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateReqDto commentUpdateReqDto) {
        CommentResDto dto = commentService.updateComment(commentId,commentUpdateReqDto);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "댓글수정이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonResDto> deleteComment(@PathVariable Long commentId) {
        CommentResDto dto = commentService.deleteComment(commentId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "댓글삭제가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<CommonResDto> getComments(
            @PathVariable Long planId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageApiResponse<CommentResDto> dtos = commentService.getCommentsByPost(planId, page, size);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "플랜기준 부모 댓글조회가 성공적으로 되었습니다.", dtos), HttpStatus.OK);
    }

    @GetMapping("/{planId}/{commentId}")
    public ResponseEntity<CommonResDto> getComments(
            @PathVariable Long planId, @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageApiResponse<CommentResDto> dtos = commentService.getCommentsByParent(planId, commentId, page, size);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "부모댓글 기준으로 자식댓글 조회가 성공적으로 되었습니다.", dtos), HttpStatus.OK);
    }
}
