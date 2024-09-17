package KickIt.server.domain.user.entity;

import KickIt.server.domain.user.OAuthProvider;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;

    private OAuthProvider oAuthProvider;

    private String nickname;

    private String firstTeam;
    private String secondTeam;
    private String thirdTeam;

    private String grade;

    private Boolean consent;

    @Builder
    public Member(String email, OAuthProvider oAuthProvider) {
        this.email = email;
        this.oAuthProvider = oAuthProvider;
    }

}