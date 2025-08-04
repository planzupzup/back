package travel.travel.bookmark.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.travel.bookmark.domain.Bookmark;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Plan;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByMemberAndPlan(Member member, Plan plan);
    long countByPlan(Plan plan);
    boolean existsByMemberAndPlan(Member member, Plan plan);

    @Query("SELECT b.plan.planId FROM Bookmark b WHERE b.member = :member")
    List<Long> findPlanIdsByMember(@Param("member") Member member);

    @Query("""
    SELECT b.plan FROM Bookmark b
    WHERE b.member = :member
    ORDER BY b.plan.createdTime ASC
    """)
    Page<Plan> findBookmarkedPlansByMember(@Param("member") Member member, Pageable pageable);

    @Query("""
    SELECT p
    FROM Bookmark bm
    JOIN bm.plan p
    LEFT JOIN p.comments c
    WHERE bm.member = :member
    GROUP BY p
    ORDER BY COUNT(c) ASC
    """)
    Page<Plan> findBookmarkedPlansByMemberOrderByCommentCount(@Param("member") Member member, Pageable pageable);

    @Query("""
    SELECT p
    FROM Bookmark bm
    JOIN bm.plan p
    LEFT JOIN p.bookmark b
    WHERE bm.member = :member
    GROUP BY p
    ORDER BY COUNT(b) ASC
    """)
    Page<Plan> findBookmarkedPlansByMemberOrderByBookmarkCount(@Param("member") Member member, Pageable pageable);


    void deleteByMember(Member member);
}
