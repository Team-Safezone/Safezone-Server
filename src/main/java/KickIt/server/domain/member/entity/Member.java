package KickIt.server.domain.member.entity;

import KickIt.server.domain.diary.entity.Diary;
import KickIt.server.domain.diary.entity.DiaryLiked;
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
    private Long id;

    private String email;

    private String nickname;

    private String team1;
    private String team2;
    private String team3;

    int avgHeartRate;

    private int point;
    private int grade;

    private Boolean marketingConsent;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    public Member(String email, String nickname, List<String> favoriteTeams, int point, int grade, Boolean marketingConsent, AuthProvider authProvider) {
        this.email = email;
        this.nickname = nickname;
        this.team1 = favoriteTeams.get(0);
        if(favoriteTeams.size() >= 2){
            this.team2 = favoriteTeams.get(1);
            if(favoriteTeams.size() >= 3) {
                this.team3 = favoriteTeams.get(2);
            }
        }
        this.point = point;
        this.grade = grade;
        this.marketingConsent = marketingConsent;
        this.authProvider = authProvider;
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Diary> diaries;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiaryLiked> likedDiaries;

}