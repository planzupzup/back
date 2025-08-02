package travel.travel.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.travel.comment.domain.Comment;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Plan;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPlanAndParentIsNull(Plan plan, Pageable pageable);

    Page<Comment> findByParent(Comment parent, Pageable pageable);

    void deleteByMember(Member member);

    @Query("""
    SELECT c FROM Comment c
    LEFT JOIN c.like l
    WHERE c.plan = :plan and c.parent is null
    GROUP BY c
    ORDER BY COUNT(l) DESC
    """)
    Page<Comment> findCommentsOrderByLikeCountAndParentIsNull(@Param("plan") Plan plan, Pageable pageable);

}