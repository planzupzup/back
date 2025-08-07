package travel.travel.plan.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.dto.DestinationResDto;
import travel.travel.plan.repository.DestinationRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class DestinationService {

    private final DestinationRepository destinationRepository;


    public List<DestinationResDto> findDestination(String map) {
        List<Destination> destinations = destinationRepository.searchByAddressContaining(map);
        if (destinations.isEmpty()) {
            throw new EntityNotFoundException("목적지가 없습니다.");
        }

        return destinations.stream()
                .map(DestinationResDto::of)
                .toList();
    }
}
