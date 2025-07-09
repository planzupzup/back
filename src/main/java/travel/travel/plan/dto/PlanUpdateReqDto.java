package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanUpdateReqDto {

    @NotNull(message = "공개/비공개 설정은 필수입니다.")
    @JsonProperty("isPublic")
    private boolean isPublic;

    @NotEmpty(message = "title은 필수입니다.")
    private String title;
    private String content;

    private LocalDate startDate;
    private LocalDate endDate;

}