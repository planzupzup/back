package travel.travel.plan.service;

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
        return destinationRepository.findByDestinationNameContaining(map)
                .stream()
                .map(DestinationResDto::of)
                .toList();
    }
}
