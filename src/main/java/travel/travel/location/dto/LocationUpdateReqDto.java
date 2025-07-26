package travel.travel.location.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateReqDto {

    @NotEmpty(message = "location name 은 필수입니다.")
    private String locationName;

    private double latitude;
    private double longitude;
    private String description;

    private double rating;
    private String placeId;
    private String googleImageUrl;
    private String types;
}
