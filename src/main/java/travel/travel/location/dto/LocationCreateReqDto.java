package travel.travel.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import travel.travel.location.domain.Location;
import travel.travel.plan.domain.Plan;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "지역 생성 요청 DTO")
public class LocationCreateReqDto {

    @Schema(description = "지역 이름", example = "경복궁")
    @NotEmpty(message = "locationName는 필수입니다.")
    private String locationName;

    @Schema(description = "위도", example = "37.5796")
    private double latitude;
    @Schema(description = "경도", example = "126.9770")
    private double longitude;
    @Schema(description = "지역 설명", example = "조선시대 궁궐의 아름다운 건축물")
    private String description;

    @Schema(description = "평점", example = "4.5")
    private double rating;
    @Schema(description = "Google Place ID", example = "ChIJ4_RdTr2ZfDURUcNTAIpOCj8")
    private String placeId;

    @Schema(description = "Google 이미지 URL", example = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=...")
    private String googleImageUrl;
    @Schema(description = "장소 유형", example = "tourist_attraction,point_of_interest,establishment")
    private String types;
    @Schema(description = "추가 이미지 URL 목록", example = "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]")
    private List<String> images;

    public static Location toEntity(LocationCreateReqDto dto, Plan plan, Integer day, Integer newOrderNumber) {
        return Location.builder()
                .locationName(dto.locationName)
                .latitude(dto.latitude)
                .longitude(dto.longitude)
                .day(day)
                .description(dto.description)
                .rating(dto.rating)
                .placeId(dto.placeId)
                .scheduleOrder(newOrderNumber)
                .plan(plan)
                .googleImageUrl(dto.googleImageUrl)
                .types(dto.types)
                .images(dto.images)
                .build();
    }
}
