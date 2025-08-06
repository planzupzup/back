package travel.travel.mypage;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MemberReqDto {

    @NotNull
    private String nickName;

    private String description;
}
