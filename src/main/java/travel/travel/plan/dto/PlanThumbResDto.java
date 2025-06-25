package travel.travel.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import travel.travel.plan.domain.Plan;

@Builder
@AllArgsConstructor
@Data
public class PlanThumbResDto {
    private Long planId;
    private String title;
    private String destinationName;

    public static PlanThumbResDto fromThumbEntity(Plan plan) {
        return PlanThumbResDto.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .destinationName(plan.getDestination().getDestinationName())
                .build();
    }

}
