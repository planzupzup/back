package travel.travel.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import travel.travel.comment.domain.Comment;
import travel.travel.plan.domain.Plan;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPlanAndParentIsNull(Plan plan);

    @Query("SELECT c FROM Comment c WHERE c.parent.commentId = :parentId")
    List<Comment> findChildCommentsByParentId(Long parentId);
}
