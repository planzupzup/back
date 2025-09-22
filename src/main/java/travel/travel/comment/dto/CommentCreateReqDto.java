package travel.travel.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import travel.travel.comment.domain.Comment;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Plan;

@Setter
@Getter
@Schema(description = "댓글 생성 요청 DTO")
public class CommentCreateReqDto {

    @Schema(description = "댓글 내용", example = "좋은 여행 계획이네요!")
    private String content;
    @Schema(description = "부모 댓글 ID (대댓글인 경우) 아닌 경우 null", example = "1")
    private Long parentId;
    @Schema(description = "계획 ID", example = "1")
    private Long planId;

    public static Comment toEntity(CommentCreateReqDto dto, Member member, Comment parent, Plan plan) {
        return Comment.builder()
                .content(dto.content)
                .parent(parent)
                .member(member)
                .plan(plan)
                .build();
    }
}
