package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import travel.travel.plan.domain.Plan;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanThumbResDto {
    private Long planId;
    private String title;
    private String destinationName;
    private boolean isBookMarked;

    @JsonProperty("isBookMarked")
    public boolean getIsBookMarked() {
        return isBookMarked;
    }

    public static PlanThumbResDto of(Plan plan, boolean bookMarked) {
        return PlanThumbResDto.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .destinationName(plan.getDestination().getDestinationName())
                .isBookMarked(bookMarked)
                .build();
    }

}
