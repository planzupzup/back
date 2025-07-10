package travel.travel.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import travel.travel.comment.domain.Comment;
import travel.travel.like.domain.Like;
import travel.travel.member.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberAndComment(Member member, Comment comment);
    long countByComment(Comment comment);
    boolean existsByMemberAndComment(Member member, Comment comment);

    @Query("SELECT l.comment.commentId FROM Like l WHERE l.member = :member")
    List<Long> findCommentIdsByMember(@Param("member") Member member);
}
