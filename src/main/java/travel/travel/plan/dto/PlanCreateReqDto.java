package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.domain.Plan;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanCreateReqDto {

    @JsonProperty("isPublic")
    private boolean isPublic;

    @NotEmpty(message = "title은 필수입니다.")
    private String title;
    private String content;

    private LocalDate startDate;
    private LocalDate endDate;

    private String destinationName;

    public static Plan toEntity(PlanCreateReqDto dto, Member member, Destination destination) {
        return Plan.builder()
                .isPublic(dto.isPublic)
                .title(dto.title)
                .content(dto.content)
                .startDate(dto.startDate)
                .endDate(dto.endDate)
                .destination(destination)
                .member(member)
                .build();
    }
}
