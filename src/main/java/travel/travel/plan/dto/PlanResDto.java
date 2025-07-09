package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import travel.travel.location.dto.LocationThumbResDto;
import travel.travel.plan.domain.Plan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class PlanResDto {
    private Long planId;

    private boolean isPublic;

    private String nickName;
    private boolean isBookMarked;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;

    private String destinationName;

    private List<LocationThumbResDto> locations;

    @JsonProperty("isPublic")
    public boolean getIsPublic() {
        return isPublic;
    }

    @JsonProperty("isBookMarked")
    public boolean getIsBookMarked() {
        return isBookMarked;
    }

    public static PlanResDto of(Plan plan, List<LocationThumbResDto> locations, boolean bookMarked) {
        return PlanResDto.builder()
                .planId(plan.getPlanId())
                .isPublic(plan.isPublic())
                .nickName(plan.getMember().getNickName())
                .isBookMarked(bookMarked)
                .title(plan.getTitle())
                .content(plan.getContent())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .destinationName(plan.getDestination().getDestinationName())
                .locations(locations)
                .build();
    }

    public static PlanResDto of(Plan plan, boolean bookMarked) {
        List<LocationThumbResDto> locations = (plan.getLocations() != null && !plan.getLocations().isEmpty())
                ? plan.getLocations().stream()
                .map(LocationThumbResDto::of)
                .toList()
                : new ArrayList<>();
        return of(plan, locations, bookMarked);
    }

}
