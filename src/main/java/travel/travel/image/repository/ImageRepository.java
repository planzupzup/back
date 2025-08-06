package travel.travel.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel.image.domain.Image;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageUrl(String url);
}
