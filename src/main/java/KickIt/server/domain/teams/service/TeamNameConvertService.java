package KickIt.server.domain.teams.service;

import KickIt.server.domain.teams.dto.EplTeamDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 시즌 전체 팀 이름 정보 DB에서 GET 해 오는 service 사용 -> 받아 온 리스트에서 이름 변환해 주는 service
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


    // 영어 이름 -> 한국어 이름
    // TOT -> 토트넘
    public String convertToKrName(String team){
        String krName = responseList.stream()
                .filter(eplTeam -> eplTeam.getTeam().equalsIgnoreCase(team))
                .map(EplTeamDto.EplTeamResponse::getKrName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        return krName;
    }

    // 영어 이름 -> 한국어 풀네임
    // TOT -> Tottenham Hotspur FC
    public String convertToEngName(String team){
        String engName = responseList.stream()
                .filter(eplTeam -> eplTeam.getTeam().equalsIgnoreCase(team))
                .map(EplTeamDto.EplTeamResponse::getEngName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        return engName;
    }

    // 영어 이름 -> 영어 풀네임
    // TOT -> 토트넘 홋스퍼 Fc
    public String convertToKrFullName(String team){
        String krFullName = responseList.stream()
                .filter(eplTeam -> eplTeam.getTeam().equalsIgnoreCase(team))
                .map(EplTeamDto.EplTeamResponse::getKrFullName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        return krFullName;
    }

    // 한국어 이름 -> 영어 이름
    // 토트넘 -> TOT
    public String convertFromKrName(String krName){
        String team = responseList.stream()
                .filter(eplTeam -> eplTeam.getKrName().equalsIgnoreCase(krName))
                .map(EplTeamDto.EplTeamResponse::getTeam)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        return team;
    }

    // 한국어 풀네임 -> 영어 이름
    // 토트넘 홋스퍼 -> TOT
    public String convertFromKrFullName(String krFullName){
        String team = responseList.stream()
                .filter(eplTeam -> eplTeam.getKrFullName().equalsIgnoreCase(krFullName))
                .map(EplTeamDto.EplTeamResponse::getTeam)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        return team;
    }

    // 영어 풀네임 -> 영어 이름
    // Tottenham Hotspur -> TOT
    public String convertFromEngName(String engName){
        String team = responseList.stream()
                .filter(eplTeam -> eplTeam.getEngName().equalsIgnoreCase(engName))
                .map(EplTeamDto.EplTeamResponse::getTeam)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("팀 이름을 찾을 수 없습니다."));
        return team;
    }
}
