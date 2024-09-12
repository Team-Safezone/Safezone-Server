package KickIt.server.domain.teams.entity;

import KickIt.server.domain.teams.PlayerPosition;
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
    private String team;
    // 선수 등번호
    // 선수 번호 데이터가 없는 경우 null 값 주므로 이를 수용하기 위해 int -> Integer로 변경
    private Integer number;
    // 선수 이름
    private String name;
    // 선수 포지션
    private PlayerPosition position;
    // 이미지 주소
    private String profileImg;
}
