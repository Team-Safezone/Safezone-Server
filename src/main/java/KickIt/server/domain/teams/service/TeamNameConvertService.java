package KickIt.server.domain.teams.service;

import KickIt.server.domain.teams.dto.EplTeamDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamNameConvertService {
    private final EplTeamService eplTeamService;
    List<EplTeamDto.EplTeamResponse> responseList;

    public TeamNameConvertService(EplTeamService eplTeamService){
        this.eplTeamService = eplTeamService;
        this.responseList = eplTeamService.FindAllEplTeams();
    }

    public String convertToKrName(String team){
        String krName = responseList.stream()
                .filter(eplTeam -> eplTeam.getTeam().equalsIgnoreCase(team))
                .map(EplTeamDto.EplTeamResponse::getKrName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        System.out.println(krName);
        return krName;
    }
}
