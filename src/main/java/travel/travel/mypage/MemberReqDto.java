package travel.travel.mypage;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberReqDto {

    @NotNull
    private String nickName;

    private String description;
}
