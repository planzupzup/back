package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDto {
    private String name;
    private List<String> types;

    @JsonProperty("formatted_address")
    private String formattedAddress;

    private double latitude;
    private double longitude;

    private Double rating;
    private String photoUrl;

}
