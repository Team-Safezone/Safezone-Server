package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.lineup.entity.MatchLineup;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.global.util.WebDriverUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class test {

    public static void main(String[] args) {
        // fixtureCrawler 테스트 및 출력
        FixtureCrawler mayFixtureCrawler = new FixtureCrawler();
        String year = String.valueOf(LocalDate.now().getYear());
        //String month = String.format("%02d", LocalDate.now().getMonthValue());
        String month = "05";
        List<Fixture> fixtureList = mayFixtureCrawler.getFixture(year, month);


        for(int i = 0; i < fixtureList.size(); i++){
            Fixture fixture = fixtureList.get(i);
            Logger.getGlobal().log(Level.INFO, String.format("%s\n%s\n%s\n%s vs %s\n%s : %s\n%sR\n%s\n%s\n",
                    fixture.getId(), fixture.getSeason(), fixture.getDate(), fixture.getHomeTeam(), fixture.getAwayTeam(),
                    fixture.getHomeTeamScore(), fixture.getAwayteamScore(), fixture.getRound(), fixture.getStatus(), fixture.getLineupUrl()));
        }

        /*
        LineupCrawler lineupCrawler = new LineupCrawler();
        MatchLineup lineup = lineupCrawler.getLineup(fixtureList.get(0));
        if (lineup != null){
            Logger.getGlobal().log(Level.INFO, String.format("%s\n %s\n %s\n %s\n %s\n %s\n %s\n", lineup.getId(), lineup.getHomeTeam(), lineup.getAwayTeam(), lineup.getHomeTeamForm(), lineup.getAwayTeamForm(), lineup.getHomeTeamLineup().getDirector(), lineup.getAwayTeamLineup().getDirector()));

            ArrayList<List<Player>> homePlayers = lineup.getHomeTeamLineup().getPlayers();
            for (int i = 0; i < homePlayers.size(); i++){
                List<Player> homeSubPlayers = homePlayers.get(i);
                for(int j = 0; j < homeSubPlayers.size(); j++){
                    Player homePlayer = homeSubPlayers.get(j);
                    Logger.getGlobal().log(Level.INFO, String.format("hometeam 선수 명단 %s\n %s %s %s %s\n", i, homePlayer.getId(),
                            homePlayer.getNumber(), homePlayer.getName(), homePlayer.getPosition()));
                }
            }

            ArrayList<List<Player>> awayPlayers = lineup.getAwayTeamLineup().getPlayers();
            for (int i = 0; i < awayPlayers.size(); i++){
                List<Player> awaySubPlayers = awayPlayers.get(i);
                for(int j = 0; j < awaySubPlayers.size(); j++){
                    Player awayPlayer = awaySubPlayers.get(j);
                    Logger.getGlobal().log(Level.INFO, String.format("awayteam 선수 명단 %s\n %s %s %s %s\n", i, awayPlayer.getId(),
                            awayPlayer.getNumber(), awayPlayer.getName(), awayPlayer.getPosition()));
                }
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

        // PlayerCralwer 테스트 및 출력
        SquadCrawler squadCrawler = new SquadCrawler();
        squadCrawler.getTeamSquads();
        */
    }
}