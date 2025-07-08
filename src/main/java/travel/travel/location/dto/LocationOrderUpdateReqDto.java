package travel.travel.location.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationOrderUpdateReqDto {
    private Long locationId;
    private Integer day;
    private Integer scheduleOrder;
}