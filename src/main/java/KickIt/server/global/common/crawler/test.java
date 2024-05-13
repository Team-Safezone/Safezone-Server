package KickIt.server.global.common.crawler;

import KickIt.server.domain.fixture.entity.Fixture;

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
        for(int i = 0; i < fixtureList.size(); i++){
            Fixture fixture = fixtureList.get(i);
            Logger.getGlobal().log(Level.INFO, String.format("%s\n%s\n%s\n%s vs %s\n%s : %s\n%sR\n%s\n",
                    fixture.getId(), fixture.getSeason(), fixture.getDate(), fixture.getHomeTeam(), fixture.getAwayTeam(),
                    fixture.getHomeTeamScore(), fixture.getAwayteamScore(), fixture.getRound(), fixture.getStatus()));
        }
    }
}

