package travel.travel.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.comment.domain.Comment;
import travel.travel.plan.domain.Plan;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPlanAndParentIsNull(Plan plan, Pageable pageable);
}
