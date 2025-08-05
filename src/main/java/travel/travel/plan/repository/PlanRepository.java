package travel.travel.plan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Query("""
        SELECT p FROM Plan p
        LEFT JOIN p.destination d
        WHERE p.isPublic = true
        AND (
            p.title LIKE %:keyword%
            OR p.content LIKE %:keyword%
            OR d.destinationName LIKE %:keyword%
        )
    """)
    Page<Plan> searchByKeywordAndIsPublicTrue(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
    SELECT p FROM Plan p
    LEFT JOIN p.destination d
    LEFT JOIN p.bookmark b
    WHERE p.isPublic = true
        AND (
            p.title LIKE %:keyword%
            OR p.content LIKE %:keyword%
            OR d.destinationName LIKE %:keyword%
        )
    GROUP BY p
    ORDER BY COUNT(b) DESC
    """)
    Page<Plan> searchByKeywordAndOrderByBookmarkCountAndIsPublicTrue(@Param("keyword") String keyword,  Pageable pageable);

    @Query("""
        SELECT p FROM Plan p
        LEFT JOIN p.destination d
        LEFT JOIN p.comments c
        WHERE p.isPublic = true
        AND (
            p.title LIKE %:keyword%
            OR p.content LIKE %:keyword%
            OR d.destinationName LIKE %:keyword%
        )
        GROUP BY p
        ORDER BY COUNT(c) DESC
    """)
    Page<Plan> searchByKeywordAndOrderByCommentCountAndIsPublicTrue(@Param("keyword") String keyword, Pageable pageable);

    Page<Plan> findAllByIsPublicTrue(Pageable pageable);

    Page<Plan> findByMember(Member member, Pageable pageable);

    Page<Plan> findByIsPublicAndMember(boolean b, Member member, Pageable pageable);
}
