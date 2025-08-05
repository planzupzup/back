package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import travel.travel.plan.domain.Plan;

import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanThumbResDto {
    private Long planId;
    private String nickName;
    private String profileImage;
    private String title;
    private Long days;
    private String destinationName;
    private boolean isBookMarked;

    @JsonProperty("isBookMarked")
    public boolean getIsBookMarked() {
        return isBookMarked;
    }

    private Integer bookMarkCount;
    private Integer commentCount;

    public static PlanThumbResDto of(Plan plan, boolean bookMarked) {
        return PlanThumbResDto.builder()
                .planId(plan.getPlanId())
                .nickName(plan.getMember().getNickName())
                .profileImage(plan.getMember().getImageUrl())
                .title(plan.getTitle())
                .days(ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate())+1)
                .destinationName(plan.getDestination().getDestinationName())
                .isBookMarked(bookMarked)
                .bookMarkCount(plan.getBookmark() == null ? 0 : plan.getBookmark().size())
                .commentCount(plan.getComments() == null ? 0 : plan.getComments().size())
                .build();
    }

}
