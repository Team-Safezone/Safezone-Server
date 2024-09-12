package KickIt.server.domain.teams.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teamInfo")
// 시즌 별 팀 정보
public class Teaminfo {
    @Id
    // 팀 랭킹
    private int ranking;
    // 팀 이름
    private String team;
    // 팀 로고 url
    private String logoUrl;
    // 시즌 정보
    private String season;

    //승점 등 정보 필요시 추가로 작성 예정

}