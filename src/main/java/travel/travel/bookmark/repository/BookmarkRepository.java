package travel.travel.bookmark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.bookmark.domain.Bookmark;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Plan;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByMemberAndPlan(Member member, Plan plan);
    long countByPlan(Plan plan);
}
