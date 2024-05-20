package KickIt.server.domain.teams.entity;

import KickIt.server.domain.teams.EplTeams;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// 한 선수의 정보를 담을 class Player
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    // 선수 고유 id
    private UUID id;
    // 선수 소속팀
    private EplTeams team;
    // 선수 등번호
    private int number;
    // 선수 이름
    private String name;
}
