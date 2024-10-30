package KickIt.server.domain.lineup.dto;

import KickIt.server.domain.fixture.dto.ResponsePlayerInfo;
import KickIt.server.domain.lineup.entity.MatchLineup;
import KickIt.server.domain.teams.entity.Player;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MatchLineupDto {
    @Getter
    public class MatchLineupResponse{
        private String homeFormation;
        private String awayFormation;
        private MatchPosPlayersDto homeLineups;
        private MatchPosPlayersDto awayLineups;
        private List<ResponsePlayerInfo> homeSubstitutes;
        private List<ResponsePlayerInfo> awaySubstitutes;
        private String homeDirector;
        private String awayDirector;

        public MatchLineupResponse(MatchLineup matchLineup){
            this.homeFormation = matchLineup.getHomeTeamLineup().getForm();
            this.awayFormation = matchLineup.getAwayTeamLineup().getForm();
            this.homeSubstitutes = new ArrayList<>();
            this.awaySubstitutes = new ArrayList<>();
            this.homeDirector = matchLineup.getHomeDirector();
            this.awayDirector = matchLineup.getAwayDirector();

            // 홈팀 선수 명단에서 포메이션대로 자른 선수 List를 전체 List에 순서대로 정렬해 추가하는 과정
            String[] homeFormNum = matchLineup.getHomeTeamLineup().getForm().split("-");
            List<List<Player>> homePlayers = new ArrayList<>();
            List<List<ResponsePlayerInfo>> homePosPlayers = new ArrayList<>();
            List<Player> homeGK = new ArrayList<>();
            homeGK.add(matchLineup.getHomeTeamLineup().getPlayers().get(0));
            homePlayers.add(homeGK);

            int startIndex = 1;
            int endIndex = Integer.parseInt(homeFormNum[0]) + 1;
            List<Player> tempPlayers = new ArrayList<>(matchLineup.getHomeTeamLineup().getPlayers().subList(startIndex, endIndex));
            Collections.reverse(tempPlayers);
            homePlayers.add(tempPlayers);
            for(int i = 1; i < homeFormNum.length; i++){
                startIndex = endIndex;
                endIndex = startIndex + Integer.parseInt(homeFormNum[i]);
                tempPlayers = new ArrayList<>(matchLineup.getHomeTeamLineup().getPlayers().subList(startIndex, endIndex));
                Collections.reverse(tempPlayers);
                homePlayers.add(tempPlayers);
            }
            for(List<Player> players : homePlayers){
                List<ResponsePlayerInfo> playerDtos = new ArrayList<>();
                for(Player player : players){
                    playerDtos.add(new ResponsePlayerInfo(player));
                }
                homePosPlayers.add(playerDtos);
            }
            this.homeLineups = new MatchPosPlayersDto(homePosPlayers);

            // 원정팀 선수 명단에서 포메이션대로 자른 선수 List를 전체 List에 순서대로 정렬해 추가하는 과정
            String[] awayFormNum = matchLineup.getAwayTeamLineup().getForm().split("-");
            List<List<Player>> awayPlayers = new ArrayList<>();
            List<List<ResponsePlayerInfo>> awayPosPlayers = new ArrayList<>();
            List<Player> awayGK = new ArrayList<>();
            awayGK.add(matchLineup.getAwayTeamLineup().getPlayers().get(0));
            awayPlayers.add(awayGK);

            startIndex = 1;
            endIndex = Integer.parseInt(awayFormNum[0]) + 1;
            tempPlayers = new ArrayList<>(matchLineup.getAwayTeamLineup().getPlayers().subList(startIndex, endIndex));
            Collections.reverse(tempPlayers);
            awayPlayers.add(tempPlayers);
            for(int i = 1; i < awayFormNum.length; i++){
                startIndex = endIndex;
                endIndex = startIndex + Integer.parseInt(awayFormNum[i]);
                tempPlayers = new ArrayList<>(matchLineup.getAwayTeamLineup().getPlayers().subList(startIndex, endIndex));
                Collections.reverse(tempPlayers);
                awayPlayers.add(tempPlayers);
            }
            for(List<Player> players : awayPlayers){
                List<ResponsePlayerInfo> playerDtos = new ArrayList<>();
                for(Player player : players){
                    playerDtos.add(new ResponsePlayerInfo(player));
                }
                awayPosPlayers.add(playerDtos);
            }
            this.awayLineups = new MatchPosPlayersDto(awayPosPlayers);

            // 홈팀 후보선수 명단에서 선수 이름만 가져와 리스트로 만들어 dto에 저장
            for(Player player : matchLineup.getHomeTeamLineup().getBenchPlayers()){
                this.homeSubstitutes.add(new ResponsePlayerInfo(player));
            }

            // 원정팀 후보선수 명단에서 선수 이름만 가져와 리스트로 만들어 dto에 저장
            for(Player player : matchLineup.getAwayTeamLineup().getBenchPlayers()){
                this.awaySubstitutes.add(new ResponsePlayerInfo(player));
            }
        }
    }

    @Getter
    public class MatchPosPlayersDto{
        private List<ResponsePlayerInfo> goalkeeper;
        private List<ResponsePlayerInfo> defenders;
        private List<ResponsePlayerInfo> midfielders;
        private List<ResponsePlayerInfo> secondMidFielders;
        private List<ResponsePlayerInfo> strikers;

        public MatchPosPlayersDto(List<List<ResponsePlayerInfo>> players){
            if(players.size() == 5){
                goalkeeper = players.get(0);
                defenders = players.get(1);
                midfielders = players.get(2);
                secondMidFielders = players.get(3);
                strikers = players.get(4);
            }
            else{
                goalkeeper = players.get(0);
                defenders = players.get(1);
                midfielders = players.get(2);
                strikers = players.get(3);
            }
        }
    }
}
