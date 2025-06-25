package travel.travel.plan.dto;

import lombok.*;
import travel.travel.location.dto.LocationThumbResDto;
import travel.travel.plan.domain.Plan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResDto {
    private Long planId;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long destinationId;
    private String destinationName;

    private List<LocationThumbResDto> locations;

    public static PlanResDto fromEntity(Plan plan) {
        return PlanResDto.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .content(plan.getContent())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .destinationId(plan.getDestination().getDestinationId())
                .destinationName(plan.getDestination().getDestinationName())
                .locations(
                        (plan.getLocations() != null && !plan.getLocations().isEmpty())
                                ? plan.getLocations().stream().map(LocationThumbResDto::fromThumbEntity).toList()
                                : new ArrayList<>()
                )
                .build();
    }

    public static PlanResDto fromEntityByDay(Plan plan, List<LocationThumbResDto> locations) {
        return PlanResDto.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .content(plan.getContent())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .destinationId(plan.getDestination().getDestinationId())
                .destinationName(plan.getDestination().getDestinationName())
                .locations(locations)
                .build();
    }

}
