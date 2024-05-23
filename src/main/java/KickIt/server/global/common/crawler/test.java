package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.lineup.entity.MatchLineup;
import KickIt.server.domain.teams.entity.Player;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class test {

    public static void main(String[] args) {

        FixtureCrawler mayFixtureCrawler = new FixtureCrawler();
        String year = String.valueOf(LocalDate.now().getYear());
        String month = String.format("%02d", LocalDate.now().getMonthValue());
        List<Fixture> fixtureList = mayFixtureCrawler.getFixture(year, month);

        // fixtureCrawler 테스트 및 출력
        /*
        for(int i = 0; i < fixtureList.size(); i++){
            Fixture fixture = fixtureList.get(i);
            Logger.getGlobal().log(Level.INFO, String.format("%s\n%s\n%s\n%s vs %s\n%s : %s\n%sR\n%s\n%s\n",
                    fixture.getId(), fixture.getSeason(), fixture.getDate(), fixture.getHomeTeam(), fixture.getAwayTeam(),
                    fixture.getHomeTeamScore(), fixture.getAwayteamScore(), fixture.getRound(), fixture.getStatus(), fixture.getLineupUrl()));
        }
        */
        
        LineupCrawler lineupCrawler = new LineupCrawler();
        MatchLineup lineup = lineupCrawler.getLineup(fixtureList.get(0));
        if (lineup != null){
            Logger.getGlobal().log(Level.INFO, String.format("%s\n %s\n %s\n %s\n %s\n %s\n %s\n", lineup.getId(), lineup.getHomeTeam(), lineup.getAwayTeam(), lineup.getHomeTeamForm(), lineup.getAwayTeamForm(), lineup.getHomeTeamLineup().getDirector(), lineup.getAwayTeamLineup().getDirector()));
            for (int i = 0; i < 11; i++){
                Player player =  lineup.getHomeTeamLineup().getPlayers().get(i);
                Logger.getGlobal().log(Level.INFO, String.format("hometeam 선수 명단 \n %s %s %s %s\n", player.getId(), player.getNumber(), player.getName(), player.getPosition()));
            }
            for (int i = 0; i < 11; i++){
                Player player =  lineup.getAwayTeamLineup().getPlayers().get(i);
                Logger.getGlobal().log(Level.INFO, String.format("awayteam 선수 명단 \n %s %s %s %s\n", player.getId(), player.getNumber(), player.getName(), player.getPosition()));
            }
            for (int i = 0; i < lineup.getHomeTeamLineup().getBenchPlayers().size(); i++){
                Player benchPlayer =  lineup.getHomeTeamLineup().getBenchPlayers().get(i);
                Logger.getGlobal().log(Level.INFO, String.format("hometeam 후보 선수 명단 \n %s %s %s %s\n", benchPlayer.getId(), benchPlayer.getNumber(), benchPlayer.getName(), benchPlayer.getPosition()));
            }
            for (int i = 0; i < lineup.getAwayTeamLineup().getBenchPlayers().size(); i++){
                Player benchPlayer =  lineup.getAwayTeamLineup().getBenchPlayers().get(i);
                Logger.getGlobal().log(Level.INFO, String.format("awayteam 후보 선수 명단 \n %s %s %s %s\n", benchPlayer.getId(), benchPlayer.getNumber(), benchPlayer.getName(), benchPlayer.getPosition()));
            }
        }
    }
}

