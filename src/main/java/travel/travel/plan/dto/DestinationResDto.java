package travel.travel.plan.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import travel.travel.plan.domain.Destination;


@Builder
@Getter
@AllArgsConstructor
public class DestinationResDto {

    private String name;

    public static DestinationResDto of(Destination destination) {
        return DestinationResDto.builder()
                .name(destination.getDestinationName())
                .build();
    }
}
