package KickIt.server.domain.teams.entity;

import KickIt.server.domain.lineup.entity.TeamLineup;
import KickIt.server.domain.teams.PlayerPosition;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

// 한 선수의 정보를 담을 class Player
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    // 선수 고유 id
    @Id
    private UUID id;
    // 선수 소속팀
    private String team;
    // 선수 등번호
    // 선수 번호 데이터가 없는 경우 null 값 주므로 이를 수용하기 위해 int -> Integer로 변경
    private Integer number;
    // 선수 이름
    private String name;
    // 선수 포지션
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    private PlayerPosition position;
    // 이미지 주소
    private String profileImg;

    // squad와의 관계 설정(여러 player가 포함되는 일대다)
    @ManyToOne
    @JoinColumn(name = "squad_id")
    @JsonBackReference
    private Squad squad;

    // 각 player들이 속할 teamlineup list
    @ManyToMany(mappedBy = "players")
    private List<TeamLineup> teamLineups;

    // Squad를 설정하는 메소드
    public void assignSquad(Squad squad) {
        this.squad = squad;
    }
}
