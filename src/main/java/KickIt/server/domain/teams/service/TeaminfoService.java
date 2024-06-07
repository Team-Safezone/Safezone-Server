package KickIt.server.domain.teams.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.teams.controller.TeaminfoController;
import KickIt.server.domain.teams.entity.Teaminfo;
import KickIt.server.domain.teams.entity.TeaminfoRepository;
import KickIt.server.global.common.crawler.TeaminfoCrawler;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeaminfoService {

    // 데이터 크롤링
    private final TeaminfoCrawler teaminfoCrawler;
    // 크롤링 데이터 DB 저장
    private final TeaminfoRepository teaminfoRepository;

    public TeaminfoService( TeaminfoCrawler teaminfoCrawler, TeaminfoRepository teaminfoRepository){
        this.teaminfoRepository = teaminfoRepository;
        this.teaminfoCrawler = teaminfoCrawler;
    }

    @Transactional
    public List<Teaminfo> saveTeaminfo(int soccerSeason){
        List<Teaminfo> teaminfoList = teaminfoCrawler.getTeaminfo(soccerSeason);
        List<Teaminfo> saveTeaminfoList = new ArrayList<>();

        for(Teaminfo teaminfo: teaminfoList){
            boolean exists = teaminfoRepository.existsByRankingAndTeamAndSeason(teaminfo.getRanking(), teaminfo.getTeam(), teaminfo.getSeason());
            if(!exists){
                saveTeaminfoList.add(teaminfo);
            }
        }

        return teaminfoRepository.saveAll(saveTeaminfoList);
    }


}
