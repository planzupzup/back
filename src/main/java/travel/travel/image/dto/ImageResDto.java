package travel.travel.image.dto;

import lombok.*;
import travel.travel.image.domain.Image;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResDto {
    private Long imageId;
    private String imageUrl;

    public static ImageResDto of(Image image) {
        return ImageResDto.builder()
                .imageId(image.getImageId())
                .imageUrl(image.getImageUrl())
                .build();
    }
}
