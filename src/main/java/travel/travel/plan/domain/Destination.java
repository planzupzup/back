package travel.travel.plan.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name= "destination")
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long destinationId;

    @Column(nullable = false)
    private String country;

    @Column(name = "destination_name", nullable = false)
    private String destinationName;

    @Column(nullable = false)
    private Double neLat;
    @Column(nullable = false)
    private Double neLng;

    @Column(nullable = false)
    private Double swLat;
    @Column(nullable = false)
    private Double swLng;

}
