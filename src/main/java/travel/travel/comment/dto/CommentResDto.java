package travel.travel.comment.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    private boolean isLiked;

    @JsonProperty("isLiked")
    public boolean getIsLiked() {
        return isLiked;
    }

    public static CommentResDto of(Comment comment, boolean isLiked) {
        return CommentResDto.builder()
                .commentId(comment.getCommentId())
                .nickName(comment.getMember().getNickName())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .content(comment.getContent())
                .planId(comment.getPlan().getPlanId())
                .likesCount(comment.getLike() == null ? 0 : comment.getLike().size())
                .isLiked(isLiked)
                .build();
    }
}
