package travel.travel.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import travel.travel.common.domain.BaseEntity;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String kakaoId;

    @Column(unique = true)
    private String nickName;

    private String refreshToken;

    private String description;

    @Column(length = 1000)
    private String imageUrl;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean deleted;

    public Member(String kakaoId) {
        this.kakaoId = kakaoId;
        this.role = Role.USER;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateInfo(String nickName, String description) {
        this.nickName = nickName;
        this.description = description;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void withdraw() {
        this.deleted = true;
        this.nickName = "탈퇴회원_" + generateUuid();
        this.kakaoId = generateUuid();
        this.imageUrl = null;
        this.refreshToken = null;
    }

    private String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
}
