package KickIt.server.domain.teams.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EplTeam {
    @Id
    private String team;
    @Column
    private String engName;
    @Column
    private String krName;
    @Column
    private String krFullName;
}
