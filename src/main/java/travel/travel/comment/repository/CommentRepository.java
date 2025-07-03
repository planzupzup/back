package travel.travel.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.travel.comment.domain.Comment;

import java.util.List;

import java.util.List;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.plan.planId       = :planId
            AND c.parent            IS NULL
            AND c.commentId         > :cursor
            ORDER BY c.commentId ASC
            """)
    List<Comment> findCommentsAfterCursor(@Param("planId") Long planId, @Param("cursor") Long cursor, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parent.commentId = :parentId")
    List<Comment> findChildCommentsByParentId(Long parentId);
}
