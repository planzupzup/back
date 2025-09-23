package travel.travel.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import travel.travel.location.domain.Location;


@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Location l WHERE l.plan.planId = :planId")
    void deleteAllByPlanId(@Param("planId") Long planId);

}
