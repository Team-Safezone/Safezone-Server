package KickIt.server.domain.teams.service;

import KickIt.server.domain.teams.dto.EplTeamDto;
import KickIt.server.domain.teams.entity.EplTeam;
import KickIt.server.domain.teams.entity.EplTeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EplTeamService {
    @Autowired
    private EplTeamRepository eplTeamRepository;

    // 전체 시즌 팀 이름 리스트 가지고 와 반환
    @Transactional
    public List<EplTeamDto.EplTeamResponse> FindAllEplTeams(){
        List<EplTeam> eplTeamList = eplTeamRepository.findAll();
        List<EplTeamDto.EplTeamResponse> responseList = new ArrayList<>();
        for(EplTeam eplTeam: eplTeamList){
            responseList.add(new EplTeamDto.EplTeamResponse(eplTeam));
        }
        return responseList;
    }

}
