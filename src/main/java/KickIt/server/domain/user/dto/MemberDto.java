package KickIt.server.domain.user.dto;

import KickIt.server.domain.user.OAuthProvider;
import KickIt.server.domain.user.entity.Member;
import lombok.Getter;

@Getter
public class MemberDto {
    private Long memberId;

    private String name;

    private String email;

    private OAuthProvider oAuthProvider;

    private String nickname;

    private String firstTeam;
    private String secondTeam;
    private String thirdTeam;

    private String grade;

    private Boolean consent;

    public MemberDto(Member member) {
        this.memberId = member.getMemberId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.oAuthProvider = member.getOAuthProvider();
        this.nickname = member.getNickname();
        this.firstTeam = member.getFirstTeam();
        this.secondTeam = member.getSecondTeam();
        this.thirdTeam = member.getThirdTeam();
        this.grade = member.getGrade();
        this.consent = member.getConsent();
    }
}
