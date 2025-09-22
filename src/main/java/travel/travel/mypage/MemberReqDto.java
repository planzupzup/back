package travel.travel.mypage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "사용자 정보 수정 요청 DTO")
public class MemberReqDto {

    @Schema(description = "닉네임", example = "여행러버123")
    @NotNull
    private String nickName;

    @Schema(description = "자기소개", example = "여행을 좋아하는 사람입니다.")
    private String description;
}
