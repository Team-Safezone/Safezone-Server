package KickIt.server.domain.teams.entity;

import KickIt.server.domain.teams.EplTeams;
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
// 시즌 별 팀 정보
public class Teaminfo {
    @Id
    // 팀 랭킹
    private int ranking;
    // 팀 이름
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('MCI','ARS','LIV','AVL','TOT','NEW','CHE','MUN','WHU','BHA','BOU','CRY','WOL','FUL','EVE','BRE','NFO','LUT','BUR','SHU') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin")
    private EplTeams team;
    // 팀 로고 url
    private String logoUrl;

    //승점 등 정보 필요시 추가로 작성 예정

}