package KickIt.server.domain.teamInfo;

import KickIt.server.domain.teams.EplTeams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 시즌 별 팀 정보
public class Teaminfo {
    // 팀 랭킹
    private String ranking;
    // 팀 이름
    private EplTeams team;
    // 팀 로고 url
    private String logoImg;

}