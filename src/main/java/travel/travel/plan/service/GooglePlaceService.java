package travel.travel.plan.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.dto.PlaceDto;
import travel.travel.plan.dto.PlacesV1SearchTextResDto;
import travel.travel.plan.repository.DestinationRepository;
import travel.travel.plan.repository.PlanRepository;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class GooglePlaceService {

    @Value("${google.api.key}")
    private String googleApiKey;

    private final PlanRepository planRepository;
    private final DestinationRepository destinationRepository;
    private final RestTemplate restTemplate =  new RestTemplate();


    public List<PlaceDto> searchPlaces(Long planId, String query) {
        Plan findPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 계획입니다."));

        String destinationName = findPlan.getDestination().getDestinationName();
        Destination vp = destinationRepository.findByDestinationName(destinationName)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다."));

        String q = destinationName + " " + query;

        log.info("q : {}", q);

        String endpoint = "https://places.googleapis.com/v1/places:searchText";

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Goog-Api-Key", googleApiKey);
        headers.add("X-Goog-FieldMask",
                "places.id,places.displayName,places.formattedAddress,places.location,places.types");
        headers.add("Content-Type", "application/json");

        Map<String, Object> body = Map.of(
                "textQuery", query,
                "languageCode", "ko",
                "locationRestriction", Map.of(
                        "rectangle", Map.of(
                                "low",  Map.of("latitude", vp.getSwLat(), "longitude", vp.getSwLng()),
                                "high", Map.of("latitude", vp.getNeLat(), "longitude", vp.getNeLng())
                        )
                )
        );

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
        ResponseEntity<PlacesV1SearchTextResDto> res =
                restTemplate.postForEntity(endpoint, req, PlacesV1SearchTextResDto.class);

        return res.getBody() == null ? List.of() : res.getBody().getPlaces().stream()
                        .map(p -> new PlaceDto(
                                p.getDisplayName().getText(),
                                p.getTypes(),
                                p.getFormattedAddress(),
                                p.getLocation().getLatitude(),
                                p.getLocation().getLongitude()
                )).toList();
    }
}