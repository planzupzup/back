package travel.travel.plan.dto;

import lombok.*;
import travel.travel.location.dto.LocationResDto;
import travel.travel.plan.domain.Plan;
import travel.travel.plan.domain.PlanOwnership;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class PlanResDto {
    private Long planId;
    private Long areaCode;

    private String nickName;
    private String profileImage;

    private PlanOwnership planType;
    private boolean b;

    private String title;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;

    private String destinationName;

    private List<LocationResDto> locations;

    public static PlanResDto of(Plan plan, PlanOwnership planType, boolean b, List<LocationResDto> locationResDtoList) {
        return PlanResDto.builder()
                .planId(plan.getPlanId())
                .nickName(plan.getMember().getNickName())
                .profileImage(plan.getMember().getImageUrl())
                .planType(planType)
                .b(b)
                .title(plan.getTitle())
                .content(plan.getContent())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .destinationName(plan.getDestination().getDestinationName())
                .locations(locationResDtoList)
                .areaCode(plan.getAreaCode())
                .build();
    }

}
