package travel.travel.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.travel.location.domain.Location;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationThumbResDto {

    private Long locationId;
    private String locationName;

    private double latitude;
    private double longitude;
    private String address;
    private LocalDate day;

    private Integer scheduleOrder;

    private String placeId;
    private String googleImageUrl;
    private String types;

    public static LocationThumbResDto fromThumbEntity(Location location) {
        LocalDate startDate = location.getPlan().getStartDate();

        return LocationThumbResDto.builder()
                .locationId(location.getLocationId())
                .locationName(location.getLocationName())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .day(startDate.plusDays(location.getDay()-1))
                .scheduleOrder(location.getScheduleOrder())
                .placeId(location.getPlaceId())
                .googleImageUrl(location.getGoogleImageUrl())
                .types(location.getTypes())
                .build();
    }
}