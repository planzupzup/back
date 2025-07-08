package travel.travel.plan.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Plan;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanUpdateReqDto {

    @NotEmpty(message = "title은 필수입니다.")
    private String title;
    private String content;

    private LocalDate startDate;
    private LocalDate endDate;

    public static Plan toEntity(PlanUpdateReqDto dto, Member member) {
        return Plan.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .member(member)
                .build();
    }
}