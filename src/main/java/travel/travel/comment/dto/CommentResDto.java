package travel.travel.comment.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import travel.travel.comment.domain.Comment;


@AllArgsConstructor
@Getter
@Builder
public class CommentResDto {
    private Long commentId;
    private String content;
    private Long parentId;
    private String nickName;
    private Long planId;
    private Integer likesCount;

    public static CommentResDto of(Comment comment) {
        return CommentResDto.builder()
                .commentId(comment.getCommentId())
                .nickName(comment.getMember().getNickName())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .content(comment.getContent())
                .planId(comment.getPlan().getPlanId())
                .likesCount(comment.getLike().size())
                .build();
    }
}
