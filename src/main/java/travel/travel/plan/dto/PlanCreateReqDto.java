package travel.travel.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import travel.travel.member.domain.Member;
import travel.travel.plan.domain.Destination;
import travel.travel.plan.domain.Plan;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "여행 계획 생성 요청 DTO")
public class PlanCreateReqDto {

    @Schema(description = "공개/비공개 설정", example = "true")
    @JsonProperty("isPublic")
    private boolean isPublic;

    @Schema(description = "계획 제목", example = "서울 3일 여행")
    @NotEmpty(message = "title은 필수입니다.")
    private String title;
    @Schema(description = "계획 내용", example = "경복궁과 남산타워를 방문하는 여행")
    private String content;

    @Schema(description = "여행 시작일", example = "2025-09-01")
    private LocalDate startDate;
    @Schema(description = "여행 종료일", example = "2025-09-03")
    private LocalDate endDate;

    @Schema(description = "목적지 이름", example = "서울")
    private String destinationName;

    public static Plan toEntity(PlanCreateReqDto dto, Member member, Destination destination, Long areaCode) {
        return Plan.builder()
                .isPublic(dto.isPublic)
                .title(dto.title)
                .content(dto.content)
                .startDate(dto.startDate)
                .endDate(dto.endDate)
                .destination(destination)
                .member(member)
                .areaCode(areaCode)
                .build();
    }
}
