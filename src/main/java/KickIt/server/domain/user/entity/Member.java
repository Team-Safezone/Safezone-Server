package KickIt.server.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email;

    private String nickname;

    private String team1;
    private String team2;
    private String team3;

    private String grade;

    private Boolean marketingConsent;

    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    public Member(String email, String nickname, List<String> favoriteTeams, String grade, Boolean marketingConsent, OAuthProvider oAuthProvider) {
        this.email = email;
        this.nickname = nickname;
        this.team1 = favoriteTeams.get(0);
        if(favoriteTeams.size() >= 2){
            this.team2 = favoriteTeams.get(1);
            if(favoriteTeams.size() >= 3) {
                this.team3 = favoriteTeams.get(2);
            }
        }
        this.grade = grade;
        this.marketingConsent = marketingConsent;
        this.oAuthProvider = oAuthProvider;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }

}