package travel.travel.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import travel.travel.location.domain.Location;
import travel.travel.plan.domain.Plan;


@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    void deleteLocationsByPlan(Plan plan);
}
