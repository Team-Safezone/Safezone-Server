package KickIt.server.domain.teams.service;

import KickIt.server.domain.teams.dto.SquadDto;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.domain.teams.entity.PlayerRepository;
import KickIt.server.domain.teams.entity.Squad;
import KickIt.server.domain.teams.entity.SquadRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SquadService {
    @Autowired
    private SquadRepository squadRepository;
    @Autowired
    private TeamNameConvertService teamNameConvertService;
    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public void saveSquads(List<Squad> squadList){
        for(Squad squad : squadList){
            Logger.getGlobal().log(Level.INFO, String.format("%s : save squads 시작", squad.getTeam()));
            Optional<Squad> existingSquad = squadRepository.findBySeasonAndTeam(squad.getSeason(), squad.getTeam());
            // 만약 이미 해당 팀 / 해당 시즌의 선수 목록 존재하는 경우 업데이트
            if(existingSquad.isPresent()){
                Logger.getGlobal().log(Level.INFO, String.format("%s : 이미 존재함 -> update", squad.getTeam()));
                Squad updatedSquad = Squad.builder()
                        .id(existingSquad.get().getId())
                        .season(squad.getSeason())
                        .team(squad.getTeam())
                        .logoImg(squad.getLogoImg())
                        .players(existingSquad.get().getPlayers())
                        .build();
                List<Player> squadExistingPlayer = existingSquad.get().getPlayers();
                for (Player newPlayer : squad.getPlayers()){
                    // DB에 같은 Id 가진 Player 있는지 확인
                    Optional<Player> DBExistingPlayer = playerRepository.findByName(newPlayer.getName());
                    if (DBExistingPlayer.isPresent()) {
                        // squad 내에 이미 Player 추가되지 않은 경우 Player 추가 작업
                        if(! squadExistingPlayer.contains(DBExistingPlayer.get())){
                            // 기존 Player 존재하므로 다시 저장하지 못 하게 그대로 정보 가져와
                            // team 변경 및 정보 업데이트
                            Player updatedPlayer = Player.builder()
                                    .id(DBExistingPlayer.get().getId())
                                    .team(newPlayer.getTeam())
                                    .number(newPlayer.getNumber())
                                    .name(newPlayer.getName())
                                    .position(newPlayer.getPosition())
                                    .profileImg(newPlayer.getProfileImg())
                                    .teamLineups(DBExistingPlayer.get().getTeamLineups())
                                    .build();
                            // squad에 team만 변경한 Player 추가
                            updatedSquad.addPlayers(Collections.singletonList(updatedPlayer));
                        }
                    }
                    else{
                        updatedSquad.addPlayers(Collections.singletonList(newPlayer));
                    }
                }
                squadRepository.save(updatedSquad);
            }
            // 아닌 경우 새로 저장
            else{
                Logger.getGlobal().log(Level.INFO, String.format("%s : 새로 저장", squad.getTeam()));
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
        Collections.sort(squadNames);
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

    // 현재(최근) 시즌 팀 이름과 Url 반환
    @Transactional
    public List<SquadDto.EplNameUrlResponse> getEplTeamNameAndUrl(){
        String presentSeason = squadRepository.getNewestSeason().orElse(null);
        if(presentSeason == null){
            return null;
        }
        List<SquadDto.EplNameUrlResponse> response = new ArrayList<>();
        List<Squad> squadList = squadRepository.findBySeason(presentSeason);
        for(Squad squad : squadList){
            response.add(SquadDto.EplNameUrlResponse.builder()
                    .teamName(teamNameConvertService.convertToKrName(squad.getTeam()))
                    .teamUrl(squad.getLogoImg())
                    .build());
        }
        return response;
    }
}
