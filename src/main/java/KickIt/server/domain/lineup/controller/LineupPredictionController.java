package KickIt.server.domain.lineup.controller;

import KickIt.server.domain.fixture.entity.Fixture;
import KickIt.server.domain.fixture.entity.FixtureRepository;
import KickIt.server.domain.lineup.dto.LineupPredictionDto;
import KickIt.server.domain.lineup.entity.LineupPrediction;
import KickIt.server.domain.lineup.entity.LineupPredictionRepository;
import KickIt.server.domain.lineup.entity.PredictionPlayer;
import KickIt.server.domain.lineup.service.LineupPredictionService;
import KickIt.server.domain.member.dto.MemberRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.teams.entity.Player;
import KickIt.server.domain.teams.entity.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/lineup-predict")
public class LineupPredictionController {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FixtureRepository fixtureRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    LineupPredictionService lineupPredictionService;

    // 입력 받은 정보 바탕으로 사용자의 경기 선발 라인업 예측을 DB에 POST
    // JWT 될 때까지 일단 member id로 처리 -> 차후 변경 예정
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveLineupPrediction(@RequestParam("memberId") long memberId, @RequestParam("matchId") long matchId, @RequestBody LineupPredictionDto.LineUpPredictionRequest lineUpPredictionRequest){
        Optional<Member> foundMember = memberRepository.findById(memberId);
        Map<String, Object> responseBody = new HashMap<>();
        // id에 해당하는 사용자 존재하는 경우
        if(foundMember.isPresent()){
            Optional<Fixture> foundFixture = fixtureRepository.findById(matchId);
            // id에 해당하는 경기 존재하는 경우
            if(foundFixture.isPresent()){
                String homeTeam = foundFixture.get().getHomeTeam();
                String awayTeam = foundFixture.get().getAwayTeam();
                List<PredictionPlayer> predictionPlayer = new ArrayList<>();
                // 홈팀 선수 명단 predictionPlayer로 변환 후 List에 추가
                // 골키퍼 처리
                if(! convertPlayer(lineUpPredictionRequest.getHomeGoalkeeper(), homeTeam, predictionPlayer, 0, 0, 0)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 홈팀 골키퍼 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 수비수 처리
                if(! convertPlayer(lineUpPredictionRequest.getHomeDefenders(), homeTeam, predictionPlayer, 0, 1)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 홈팀 수비수 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 미드필더 처리
                if(! convertPlayer(lineUpPredictionRequest.getHomeMidfielders(), homeTeam, predictionPlayer, 0, 2)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 홈팀 미드필더 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 공격수 처리
                if (! convertPlayer(lineUpPredictionRequest.getHomeStrikers(), homeTeam, predictionPlayer, 0, 3)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 홈팀 공격수 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 원정팀 선수 명단 predictionPlayer로 변환 후 List에 추가
                // 골키퍼 처리
                if(! convertPlayer(lineUpPredictionRequest.getAwayGoalkeeper(), awayTeam, predictionPlayer, 1, 0, 0)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 원정팀 골키퍼 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 수비수 처리
                if(! convertPlayer(lineUpPredictionRequest.getAwayDefenders(), awayTeam, predictionPlayer, 1, 1)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 원정팀 수비수 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 미드필더 처리
                if(! convertPlayer(lineUpPredictionRequest.getAwayMidfielders(), awayTeam, predictionPlayer, 1, 2)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 원정팀 미드필더 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 공격수 처리
                if (! convertPlayer(lineUpPredictionRequest.getAwayStrikers(), awayTeam, predictionPlayer, 1, 3)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 원정팀 공격수 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // save 처리
                LineupPrediction lineupPrediction = LineupPrediction.builder()
                        .member(foundMember.get())
                        .fixture(foundFixture.get())
                        .homeTeamForm(lineUpPredictionRequest.getHomeFormation())
                        .awayTeamForm(lineUpPredictionRequest.getAwayFormation())
                        .players(predictionPlayer).build();

                lineupPredictionService.saveLineupPredictions(lineupPrediction);

                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "success");
                responseBody.put("data", new LineupPredictionDto.LineupSaveResponse(foundMember.get()));
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            // id에 해당하는 경기 존재하지 않는 경우 -> 경기 id 잘못됨
            else{
                responseBody.put("status", HttpStatus.NOT_FOUND.value());
                responseBody.put("message", "해당 경기 없음");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }
        }
        // id에 해당하는 사용자 존재하지 않는 경우 -> 사용자 id 잘못됨
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "해당 사용자 없음");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    // 입력 받은 정보 바탕으로 사용자의 기존 경기 선발 라인업 예측을 수정
    // JWT 될 때까지 일단 member id로 처리 -> 차후 변경 예정
    @PatchMapping("/edit")
    public ResponseEntity<Map<String, Object>> editLineupPrediction(@RequestParam("memberId") long memberId, @RequestParam("matchId") long matchId, @RequestBody LineupPredictionDto.LineUpPredictionRequest lineUpPredictionRequest){
        Optional<Member> foundMember = memberRepository.findById(memberId);
        Map<String, Object> responseBody = new HashMap<>();
        // id에 해당하는 사용자 존재하는 경우
        if(foundMember.isPresent()){
            Optional<Fixture> foundFixture = fixtureRepository.findById(matchId);
            // id에 해당하는 경기 존재하는 경우
            if(foundFixture.isPresent()){
                String homeTeam = foundFixture.get().getHomeTeam();
                String awayTeam = foundFixture.get().getAwayTeam();
                List<PredictionPlayer> predictionPlayer = new ArrayList<>();
                // 홈팀 선수 명단 predictionPlayer로 변환 후 List에 추가
                // 골키퍼 처리
                if(! convertPlayer(lineUpPredictionRequest.getHomeGoalkeeper(), homeTeam, predictionPlayer, 0, 0, 0)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 홈팀 골키퍼 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 수비수 처리
                if(! convertPlayer(lineUpPredictionRequest.getHomeDefenders(), homeTeam, predictionPlayer, 0, 1)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 홈팀 수비수 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 미드필더 처리
                if(! convertPlayer(lineUpPredictionRequest.getHomeMidfielders(), homeTeam, predictionPlayer, 0, 2)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 홈팀 미드필더 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 공격수 처리
                if (! convertPlayer(lineUpPredictionRequest.getHomeStrikers(), homeTeam, predictionPlayer, 0, 3)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 홈팀 공격수 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 원정팀 선수 명단 predictionPlayer로 변환 후 List에 추가
                // 골키퍼 처리
                if(! convertPlayer(lineUpPredictionRequest.getAwayGoalkeeper(), awayTeam, predictionPlayer, 1, 0, 0)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 원정팀 골키퍼 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 수비수 처리
                if(! convertPlayer(lineUpPredictionRequest.getAwayDefenders(), awayTeam, predictionPlayer, 1, 1)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 원정팀 수비수 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 미드필더 처리
                if(! convertPlayer(lineUpPredictionRequest.getAwayMidfielders(), awayTeam, predictionPlayer, 1, 2)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 원정팀 미드필더 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 공격수 처리
                if (! convertPlayer(lineUpPredictionRequest.getAwayStrikers(), awayTeam, predictionPlayer, 1, 3)){
                    // 예외 처리
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", " 원정팀 공격수 데이터 누락");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // edit 처리
                LineupPrediction lineupPrediction = LineupPrediction.builder()
                        .member(foundMember.get())
                        .fixture(foundFixture.get())
                        .homeTeamForm(lineUpPredictionRequest.getHomeFormation())
                        .awayTeamForm(lineUpPredictionRequest.getAwayFormation())
                        .players(predictionPlayer).build();

                LineupPredictionDto.LineUpPredictionEditResponse data = lineupPredictionService.editLineupPredictions(lineupPrediction);

                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "success");
                responseBody.put("data", data);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            // id에 해당하는 경기 존재하지 않는 경우 -> 경기 id 잘못됨
            else{
                responseBody.put("status", HttpStatus.NOT_FOUND.value());
                responseBody.put("message", "해당 경기 없음");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }
        }
        // id에 해당하는 사용자 존재하지 않는 경우 -> 사용자 id 잘못됨
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "해당 사용자 없음");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    // 사용자의 선발 라인업 예측 정보 조회
    // JWT 될 때까지 일단 member id로 처리 -> 차후 변경 예정
    @GetMapping()
    public ResponseEntity<Map<String, Object>> getUserLineupPrediction(@RequestParam("memberId") long memberId, @RequestParam("matchId") Long matchId){
        Optional<Member> foundMember = memberRepository.findById(memberId);
        Map<String, Object> responseBody = new HashMap<>();

        // id에 해당하는 사용자 존재하는 경우
        if(foundMember.isPresent()){
            Optional<Fixture> foundFixture = fixtureRepository.findById(matchId);
            // id에 해당하는 경기 존재하는 경우
            if(foundFixture.isPresent()){
                String homeTeam = foundFixture.get().getHomeTeam();
                String awayTeam = foundFixture.get().getAwayTeam();
                String season = foundFixture.get().getSeason();
                LineupPredictionDto.LineupInquireResponse response = lineupPredictionService.inquireLineupPrediction(matchId, memberId, homeTeam, awayTeam, season);

                // 사용자 id와 경기 id로 조회된 선발 라인업 예측 데이터 없음
                if(response == null){
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", "해당 사용자의 선발 라인업 예측 데이터 없음");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 정상적으로 가져온 response data에 넣어 반환
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "success");
                responseBody.put("data", response);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            // id에 해당하는 경기 존재하지 않는 경우 -> 경기 id 잘못됨
            else{
                responseBody.put("status", HttpStatus.NOT_FOUND.value());
                responseBody.put("message", "해당 경기 없음");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }
        }
        // id에 해당하는 사용자 존재하지 않는 경우 -> 사용자 id 잘못됨
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "해당 사용자 없음");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    // 선발 라인업 예측 결과 정보 조회 (전체)
    // JWT 될 때까지 일단 member id로 처리 -> 차후 변경 예정
    @GetMapping("/result")
    public ResponseEntity<Map<String, Object>> getLineupPrediction(@RequestParam("memberId") long memberId, @RequestParam("matchId") Long matchId) {
        Optional<Member> foundMember = memberRepository.findById(memberId);
        Map<String, Object> responseBody = new HashMap<>();

        // id에 해당하는 사용자 존재하는 경우
        if(foundMember.isPresent()){
            Optional<Fixture> foundFixture = fixtureRepository.findById(matchId);
            // id에 해당하는 경기 존재하는 경우
            if(foundFixture.isPresent()){
                String homeTeam = foundFixture.get().getHomeTeam();
                String awayTeam = foundFixture.get().getAwayTeam();
                String season = foundFixture.get().getSeason();
                // 해당 사용자가 해당 경기에 대해 선발 라인업 예측 진행해 데이터 있는지 확인
                LineupPredictionDto.LineupResultInquireResponse response = lineupPredictionService.inquireLineupPredictionResult(matchId, memberId);
                // 사용자 id와 경기 id로 조회된 선발 라인업 예측 데이터 없음
                if(response == null){
                    responseBody.put("status", HttpStatus.NOT_FOUND.value());
                    responseBody.put("message", "해당 사용자의 선발 라인업 예측 데이터 없음");
                    responseBody.put("isSuccess", false);
                    return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
                }
                // 정상적으로 가져온 response data에 넣어 반환
                responseBody.put("status", HttpStatus.OK.value());
                responseBody.put("message", "success");
                responseBody.put("data", response);
                responseBody.put("isSuccess", true);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            // id에 해당하는 경기 존재하지 않는 경우 -> 경기 id 잘못됨
            else{
                responseBody.put("status", HttpStatus.NOT_FOUND.value());
                responseBody.put("message", "해당 경기 없음");
                responseBody.put("isSuccess", false);
                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }
        }
        // id에 해당하는 경기 존재하지 않는 경우 -> 경기 id 잘못됨
        else{
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            responseBody.put("message", "해당 경기 없음");
            responseBody.put("isSuccess", false);
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        }
    }

    private Player searchPlayer(String team, int playerNum, String playerName){
        // 먼저 선수의 팀과 번호로 존재하는지 검사
        Optional<Player> player = playerRepository.findByTeamAndNumber(team, playerNum);
        if(player.isPresent()){
            return player.get();
        }
        else{
            player = playerRepository.findByTeamAndNameContaining(team, playerName);
            if(player.isPresent()){
                return player.get();
            }
            else{
                return null;
            }
        }
    }

    private boolean convertPlayer(LineupPredictionDto.RequestPlayerInfo playerInfo, String team, List<PredictionPlayer> predictionPlayer, int teamType, int position, int location){
        Player player = searchPlayer(team, playerInfo.getPlayerNum(), playerInfo.getPlayerName());
        if (player == null) {
            return false;
        }
        else {
            predictionPlayer.add(PredictionPlayer.builder()
                    .player(player)
                    .team(teamType)
                    .position(position)
                    .location(location).build());
        }
        return true;
    }

    private boolean convertPlayer(List<LineupPredictionDto.RequestPlayerInfo> playerInfos, String team, List<PredictionPlayer> predictionPlayer, int teamType, int position){
        for (int i = 0; i < playerInfos.size(); i++) {
            convertPlayer(playerInfos.get(i), team, predictionPlayer, teamType, position, i);
        }
        return true;
    }
}
