package travel.travel.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import travel.travel.plan.domain.Destination;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
    Optional<Destination> findByDestinationName(String destinationName);

    @Query("SELECT d FROM Destination d WHERE d.destinationName LIKE %:keyword%")
    List<Destination> searchByAddressContaining(@Param("keyword") String keyword);

}
