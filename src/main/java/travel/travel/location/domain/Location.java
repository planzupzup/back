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

    @OneToMany(mappedBy = "location",fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    public void updateInfo(LocationUpdateReqDto locationUpdateReqDto, List<Image> image) {
        this.locationName = locationUpdateReqDto.getLocationName();
        this.latitude = locationUpdateReqDto.getLatitude();
        this.longitude = locationUpdateReqDto.getLongitude();
        this.address = locationUpdateReqDto.getAddress();
        this.description = locationUpdateReqDto.getDescription();
        this.googleImageUrl = locationUpdateReqDto.getGoogleImageUrl();
        this.types = locationUpdateReqDto.getTypes();
        this.placeId = locationUpdateReqDto.getPlaceId();
        this.images = image;
    }

    public void updateScheduleOrder(int scheduleOrder) {
        this.scheduleOrder = scheduleOrder;
    }

    public void updateDay(int day) {
        this.day = day;
    }
}
