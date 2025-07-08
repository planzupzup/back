package travel.travel.plan.dto;

import lombok.*;
import travel.travel.location.dto.LocationThumbResDto;
import travel.travel.plan.domain.Plan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResDto {
    private Long planId;
    private String nickName;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;

    private String destinationName;

    private List<LocationThumbResDto> locations;

    public static PlanResDto of(Plan plan, List<LocationThumbResDto> locations) {
        return PlanResDto.builder()
                .planId(plan.getPlanId())
                .nickName(plan.getMember().getNickName())
                .title(plan.getTitle())
                .content(plan.getContent())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .destinationName(plan.getDestination().getDestinationName())
                .locations(locations)
                .build();
    }

    public static PlanResDto of(Plan plan) {
        List<LocationThumbResDto> locations = (plan.getLocations() != null && !plan.getLocations().isEmpty())
                ? plan.getLocations().stream()
                .map(LocationThumbResDto::of)
                .toList()
                : new ArrayList<>();
        return of(plan, locations);
    }

}
