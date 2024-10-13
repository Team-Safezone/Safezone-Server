package KickIt.server.domain.teams.service;

import KickIt.server.domain.teams.dto.SquadDto;
import KickIt.server.domain.teams.entity.Squad;
import KickIt.server.domain.teams.entity.SquadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SquadService {
    @Autowired
    private SquadRepository squadRepository;
    @Autowired
    private TeamNameConvertService teamNameConvertService;

    @Transactional
    public void saveSquads(List<Squad> squadList){
        for(Squad squad : squadList){
            Optional<Squad> existingSquad = squadRepository.findBySeasonAndTeam(squad.getSeason(), squad.getTeam());
            // 만약 이미 해당 팀 / 해당 시즌의 선수 목록 존재하는 경우 업데이트
            if(existingSquad.isPresent()){
                Squad updatedSquad = Squad.builder()
                        .id(existingSquad.get().getId())
                        .season(squad.getSeason())
                        .team(squad.getTeam())
                        .logoImg(squad.getLogoImg())
                        .build();
                updatedSquad.addFWPlayers(squad.getFWplayers());
                updatedSquad.addMFPlayers(squad.getMFplayers());
                updatedSquad.addDFPlayers(squad.getDFplayers());
                updatedSquad.addGKPlayers(squad.getGKplayers());
                squadRepository.save(updatedSquad);
            }
            // 아닌 경우 새로 저장
            else{
                squadRepository.save(squad);
            }
        }
    }

    // 해당 시즌 팀 이름만을 반환
    @Transactional
    public List<String> getSeasonSquads(String season){
        List<Squad> seasonSquads = squadRepository.findBySeason(season);
        List<String> squadNames = new ArrayList<>();
        for(Squad squad : seasonSquads){
            squadNames.add(teamNameConvertService.convertToKrName(squad.getTeam()));
        }
        return squadNames;
    }

    @Transactional
    public String getTeamLogoImg(String season, String team){
        Optional<Squad> squad = squadRepository.findBySeasonAndTeam(season, team);
        if(squad.isPresent()){
            return squad.get().getLogoImg();
        }
        return null;
    }
}
