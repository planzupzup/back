package travel.travel.mypage;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NickNameReqDto {

    @NotNull
    private String nickName;
}
