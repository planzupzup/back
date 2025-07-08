package travel.travel.location.domain;

import jakarta.persistence.*;
import lombok.*;
import travel.travel.image.domain.Image;
import travel.travel.location.dto.LocationUpdateReqDto;
import travel.travel.plan.domain.Plan;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name= "location")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    private String locationName;

    private double latitude;
    private double longitude;

    private String address;

    private Integer day;
    private String description;
    private Integer scheduleOrder;

    private String placeId;
    private String googleImageUrl;
    private String types;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "location_id")
    private List<Image> images = new ArrayList<>();

    public void updateInfo(
            String locationName,
            double latitude,
            double longitude,
            String address,
            String description,
            String googleImageUrl,
            String types,
            String placeId,
            List<Image> images
    ) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.description = description;
        this.googleImageUrl = googleImageUrl;
        this.types = types;
        this.placeId = placeId;
        this.images.clear();
        this.images.addAll(images);
    }

    public void updateScheduleOrder(int scheduleOrder) {
        this.scheduleOrder = scheduleOrder;
    }

    public void updateDay(int day) {
        this.day = day;
    }
}
