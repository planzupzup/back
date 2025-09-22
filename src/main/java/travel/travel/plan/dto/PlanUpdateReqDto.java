package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "여행 계획 수정 요청 DTO")
public class PlanUpdateReqDto {

    @Schema(description = "공개/비공개 설정", example = "false")
    @NotNull(message = "공개/비공개 설정은 필수입니다.")
    @JsonProperty("isPublic")
    private boolean isPublic;

    @Schema(description = "수정할 계획 제목", example = "서울 4일 여행")
    @NotEmpty(message = "title은 필수입니다.")
    private String title;
    @Schema(description = "수정할 계획 내용", example = "경복궁, 남산타워, 명동을 방문하는 여행")
    private String content;

    @Schema(description = "수정할 여행 시작일", example = "2025-10-01")
    private LocalDate startDate;
    @Schema(description = "수정할 여행 종료일", example = "2025-10-04")
    private LocalDate endDate;

}