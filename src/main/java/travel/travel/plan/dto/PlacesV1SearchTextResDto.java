package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacesV1SearchTextResDto {
    private List<Place> places;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Place {
        private String id;
        private DisplayName displayName;
        private String formattedAddress;
        private LatLng location;
        private List<String> types;

        private Double rating;
        private List<Photo> photos;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DisplayName {
        private String text;
        private String languageCode;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LatLng {
        private double latitude;
        private double longitude;
    }

    @Getter @NoArgsConstructor
    public static class Photo {
        private String name;
        private Integer widthPx;
        private Integer heightPx;
    }
}
