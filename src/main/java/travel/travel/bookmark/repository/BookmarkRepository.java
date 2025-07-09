package travel.travel.bookmark.repository;

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
}
