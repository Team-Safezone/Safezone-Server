package KickIt.server.domain.member.entity;

import KickIt.server.domain.diary.entity.Diary;
import KickIt.server.domain.diary.entity.DiaryLiked;
import KickIt.server.domain.diary.entity.DiaryReport;
import KickIt.server.domain.heartRate.entity.HeartRate;
import KickIt.server.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column(nullable = false, length = 10)
    private String team1;

    @Column(length = 10)
    private String team2;

    @Column(length = 10)
    private String team3;

    @Column(nullable = false)
    int avgHeartRate;

    @Column(nullable = false)
    private int point;

    @Column(nullable = false)
    private int grade;

    @Column(nullable = false)
    private Boolean marketingConsent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiaryLiked> likedDiaries = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiaryReport> diaryReports = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HeartRate> heartRateArrayList = new ArrayList<>();

    public void setPoint(int newPoint){
        this.point = newPoint;
    }
}