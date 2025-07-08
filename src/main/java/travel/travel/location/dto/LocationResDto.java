package travel.travel.location.dto;

import lombok.*;
import travel.travel.image.dto.ImageResDto;
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

    private double latitude;
    private double longitude;
    private String address;
    private LocalDate day;
    private String description;
    private Long planId;
    private Integer scheduleOrder;

    private String placeId;
    private String googleImageUrl;
    private String types;
    private List<ImageResDto> images;

    public static LocationResDto of(Location location) {
        LocalDate startDate = location.getPlan().getStartDate();
        List<ImageResDto> imageResDtos = Optional.ofNullable(location.getImages())
                .orElse(List.of())
                .stream()
                .map(img -> ImageResDto.builder()
                        .imageId(img.getImageId())
                        .imageUrl(img.getImageUrl())
                        .build())
                .toList();

        return LocationResDto.builder()
                .locationId(location.getLocationId())
                .locationName(location.getLocationName())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .day(startDate.plusDays(location.getDay()-1))
                .description(location.getDescription())
                .scheduleOrder(location.getScheduleOrder())
                .placeId(location.getPlaceId())
                .googleImageUrl(location.getGoogleImageUrl())
                .types(location.getTypes())
                .planId(location.getPlan().getPlanId())
                .images(imageResDtos)
                .build();
    }
}