package travel.travel.comment.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import travel.travel.comment.domain.Comment;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class CommentResDto {
    private Long commentId;
    private String content;
    private Long parentId;
    private String nickName;
    private Long planId;

    public static CommentResDto fromEntity(Comment comment) {
        return CommentResDto.builder()
                .commentId(comment.getCommentId())
                .nickName(comment.getMember().getNickName())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .content(comment.getContent())
                .planId(comment.getPlan().getPlanId())
                .build();
    }
}
