package travel.travel.image.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import travel.travel.image.dto.ImageResDto;
import travel.travel.location.domain.Location;


@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@DynamicInsert
@Table(name = "Image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    public ImageResDto fromEntity() {
        return ImageResDto.builder()
                .imageId(this.imageId)
                .imageUrl(this.imageUrl)
                .build();
    }
}
