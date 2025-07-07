package travel.travel.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import travel.travel.common.domain.BaseEntity;

@Getter
@Entity
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    @Column(unique = true)
    private String nickName;

    private String refreshToken;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    public Member(String kakaoId) {
        this.kakaoId = kakaoId;
        this.role = Role.USER;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }
}
