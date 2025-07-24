package travel.travel.location.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import travel.travel.image.domain.Image;
import travel.travel.location.domain.Location;
import travel.travel.plan.domain.Plan;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationCreateReqDto {

    @NotEmpty(message = "locationName는 필수입니다.")
    private String locationName;

    private double latitude;
    private double longitude;
    private String address;
    private Integer day;
    private String description;

    private String placeId;
    private String googleImageUrl;
    private String types;

    public static Location toEntity(LocationCreateReqDto dto, Plan plan, Integer newOrderNumber) {
        return Location.builder()
                .locationName(dto.locationName)
                .latitude(dto.latitude)
                .longitude(dto.longitude)
                .address(dto.address)
                .day(dto.day)
                .description(dto.description)
                .placeId(dto.placeId)
                .scheduleOrder(newOrderNumber)
                .plan(plan)
                .googleImageUrl(dto.googleImageUrl)
                .types(dto.types)
                .build();
    }
}
