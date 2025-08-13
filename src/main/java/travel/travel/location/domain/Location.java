package travel.travel.location.domain;

import jakarta.persistence.*;
import lombok.*;
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

    private String description;
    private Integer day;
    private Integer scheduleOrder;

    private double rating;
    private String placeId;
    private String googleImageUrl;
    private String types;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "location_images",
            joinColumns = @JoinColumn(name = "location_id"))
    @OrderColumn(name = "image_order")
    @Column(name = "image_url", length = 1024)
    @Builder.Default
    private List<String> images = new ArrayList<>();

}
