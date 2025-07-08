package travel.travel.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.travel.image.domain.Image;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResDto {
    private Long imageId;
    private String imageUrl;
}
