package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import travel.travel.location.dto.LocationResDto;
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
    private String profileImage;
    private boolean isBookMarked;
    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;

    private String destinationName;

    private List<LocationResDto> locations;

    @JsonProperty("isPublic")
    public boolean getIsPublic() {
        return isPublic;
    }

    @JsonProperty("isBookMarked")
    public boolean getIsBookMarked() {
        return isBookMarked;
    }

    public static PlanResDto of(Plan plan, boolean bookMarked, List<LocationResDto> locationResDtoList) {
        return PlanResDto.builder()
                .planId(plan.getPlanId())
                .isPublic(plan.isPublic())
                .nickName(plan.getMember().getNickName())
                .profileImage(plan.getMember().getImageUrl())
                .isBookMarked(bookMarked)
                .title(plan.getTitle())
                .content(plan.getContent())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .destinationName(plan.getDestination().getDestinationName())
                .locations(locationResDtoList)
                .build();
    }

}
