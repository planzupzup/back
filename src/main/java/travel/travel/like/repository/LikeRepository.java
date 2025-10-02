package travel.travel.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import travel.travel.comment.domain.Comment;
import travel.travel.like.domain.Like;
import travel.travel.member.domain.Member;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    long countByComment(Comment comment);
    boolean existsByMemberAndComment(Member member, Comment comment);

    @Query("SELECT l.comment.commentId FROM Like l WHERE l.member = :member")
    List<Long> findCommentIdsByMember(@Param("member") Member member);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.comment.commentId = :commentId")
    long countLikesByCommentId(@Param("commentId") Long commentId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Like l WHERE l.comment.commentId = :commentId AND l.member.id = :memberId")
    void deleteByCommentIdAndMemberId(@Param("commentId") Long commentId, @Param("memberId") Long memberId);

    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO `'like'` (member_id, comment_id) VALUES (:memberId, :commentId)", nativeQuery = true)
    int insertLikeIfNotExists(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

}
