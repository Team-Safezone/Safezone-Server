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
                            // team만 변경
                            Player updatedPlayer = DBExistingPlayer.get();
                            updatedPlayer.assignTeam(squad.getTeam());
                            // squad에 team만 변경한 Player 추가
                            updatedSquad.addPlayers(Collections.singletonList(updatedPlayer));
                        }
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
}
