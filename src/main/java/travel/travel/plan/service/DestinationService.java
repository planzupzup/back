package travel.travel.plan.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.dto.DestinationResDto;
import travel.travel.plan.repository.DestinationRepository;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class DestinationService {

    private final DestinationRepository destinationRepository;


    public List<DestinationResDto> findDestination(String map) {
        Optional<Destination> findDest = destinationRepository.findByDestinationName(map);
        if (findDest.isEmpty()) {
            List<Destination> findCountry = destinationRepository.findByCountry(map);
            if (!findCountry.isEmpty()) {
                return findCountry.stream()
                        .map(DestinationResDto::of)
                        .toList();
            }
            throw new EntityNotFoundException("해당 지역은 없습니다.");
        }

        return findDest.stream()
                .map(DestinationResDto::of)
                .toList();
    }
}
