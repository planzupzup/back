package travel.travel.plan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import travel.travel.plan.domain.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Query("""
        SELECT p FROM Plan p
        LEFT JOIN p.destination d
        WHERE p.title LIKE %:keyword%
        OR p.content LIKE %:keyword%
        OR d.destinationName LIKE %:keyword%
    """)
    Page<Plan> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
