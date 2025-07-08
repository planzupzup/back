package travel.travel.comment.dto;

import lombok.Getter;
import lombok.Setter;
import travel.travel.comment.domain.Comment;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Plan;

@Setter
@Getter
public class CommentCreateReqDto {

    private String content;
    private Long parentId;
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
