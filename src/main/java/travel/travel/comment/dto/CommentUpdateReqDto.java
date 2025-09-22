package travel.travel.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "댓글 수정 요청 DTO")
public class CommentUpdateReqDto {
    @Schema(description = "수정할 댓글 내용", example = "수정된 댓글 내용입니다.")
    private String content;
}
