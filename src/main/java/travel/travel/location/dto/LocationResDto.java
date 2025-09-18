package travel.travel.location.dto;

import lombok.*;
import travel.travel.location.domain.Location;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationResDto {
    private Long locationId;
    private String locationName;
    private LocalDate day;
    private Integer scheduleOrder;

    private double latitude;
    private double longitude;
    private String description;

    private double rating;
    private String placeId;
    private String googleImageUrl;
    private String types;
    private List<String> images;

    public static LocationResDto of(Location location) {
        LocalDate startDate = location.getPlan().getStartDate();
        List<String> images = Optional.ofNullable(location.getImages())
                .orElse(List.of());

        return LocationResDto.builder()
                .locationId(location.getLocationId())
                .locationName(location.getLocationName())
                .day(startDate.plusDays(location.getDay()-1))
                .scheduleOrder(location.getScheduleOrder())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())

                .description(location.getDescription())
                .rating(location.getRating())
                .placeId(location.getPlaceId())
                .googleImageUrl(location.getGoogleImageUrl())
                .types(location.getTypes())
                .images(images)
                .build();
    }
}