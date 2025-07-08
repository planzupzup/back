package travel.travel.plan.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Plan;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanUpdateReqDto {

    @NotEmpty(message = "title은 필수입니다.")
    private String title;
    private String content;

    private LocalDate startDate;
    private LocalDate endDate;

}