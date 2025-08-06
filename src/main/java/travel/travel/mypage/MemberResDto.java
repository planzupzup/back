package travel.travel.mypage;

import lombok.Builder;
import lombok.Getter;
import travel.travel.member.domain.Member;

@Builder
@Getter
public class MemberResDto {

    private String nickName;
    private String description;
    private String image;

    public static MemberResDto of(Member member) {
        return MemberResDto.builder()
                .nickName(member.getNickName())
                .description(member.getDescription())
                .image(member.getImageUrl())
                .build();
    }
}
