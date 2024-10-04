package KickIt.server.domain.lineup.service;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.lineup.dto.MatchLineupDto;
import KickIt.server.domain.lineup.entity.MatchLineup;
import KickIt.server.domain.lineup.entity.MatchLineupRepository;
import KickIt.server.global.common.crawler.LineupCrawler;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MatchLineupService {
    @Autowired
    MatchLineupRepository matchLineupRepository;
    @Autowired
    FixtureRepository fixtureRepository;
    @Autowired
    LineupCrawler lineupCrawler;

    @Transactional
    public MatchLineupDto.MatchLineupResponse findMatchLineupByFixture(Long id){
        Optional<MatchLineup> existingData = matchLineupRepository.findByFixtureId(id);
        // 경기 아이디로 일치하는 결과가 DB에 이미 있는 경우 해당 결과 반환
        if(existingData.isPresent()){
            return new MatchLineupDto().new MatchLineupResponse(existingData.get());
        }
        // 일치하는 정보 없는 경우 API 요청 들어왔을 때 크롤링 시도
        else {
            Fixture fixture = fixtureRepository.findById(id).get();
            System.out.println("경기 아이디" + fixture.getId());
            MatchLineup matchLineup = lineupCrawler.getLineup(fixture);
            // 성공 시 DB에 save 후 결과 전송
            if(matchLineup != null){
                matchLineupRepository.save(matchLineup);
                return new MatchLineupDto().new MatchLineupResponse(matchLineup);
            }
            // 실패(crawler가 타임 아웃으로 인해 null 반환하는 경우) 시 아직 선발 라인업 데이터 없는 것으로 처리
            else{
                return null;
            }
        }
    }

}
