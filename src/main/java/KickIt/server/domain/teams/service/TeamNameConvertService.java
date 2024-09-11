package KickIt.server.domain.teams.service;

import KickIt.server.domain.teams.dto.EplTeamDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamNameConvertService {
    private final EplTeamService eplTeamService;
    List<EplTeamDto.EplTeamResponse> responseList;

    @Autowired
    public TeamNameConvertService(EplTeamService eplTeamService) {
        this.eplTeamService = eplTeamService;
    }

    @PostConstruct
    public void init() {
        this.responseList = eplTeamService.FindAllEplTeams();
    }

    public String convertToKrName(String team){
        String krName = responseList.stream()
                .filter(eplTeam -> eplTeam.getTeam().equalsIgnoreCase(team))
                .map(EplTeamDto.EplTeamResponse::getKrName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        //System.out.println(krName);
        return krName;
    }

    public String convertFromKrName(String krName){
        String team = responseList.stream()
                .filter(eplTeam -> eplTeam.getKrName().equalsIgnoreCase(krName))
                .map(EplTeamDto.EplTeamResponse::getTeam)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        //System.out.println(team);
        return team;
    }

    public String convertFromKrFullName(String krFullName){
        String team = responseList.stream()
                .filter(eplTeam -> eplTeam.getKrFullName().equalsIgnoreCase(krFullName))
                .map(EplTeamDto.EplTeamResponse::getTeam)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        //System.out.println(team);
        return team;
    }

    public String convertFromEngName(String engName){
        String team = responseList.stream()
                .filter(eplTeam -> eplTeam.getEngName().equalsIgnoreCase(engName))
                .map(EplTeamDto.EplTeamResponse::getTeam)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        //System.out.println(team);
        return team;
    }
}
