package KickIt.server.domain.member.service;

import KickIt.server.domain.member.dto.MypageDto;
import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.AuthProvider;
import KickIt.server.domain.teams.entity.SquadRepository;
import KickIt.server.domain.teams.service.TeamNameConvertService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private MemberRepository memberRepository;
    private TeamNameConvertService teamNameConvertService;
    private SquadRepository squadRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository, TeamNameConvertService teamNameConvertService, SquadRepository squadRepository) {
        this.memberRepository = memberRepository;
        this.teamNameConvertService = teamNameConvertService;
        this.squadRepository = squadRepository;
    }

    @Transactional
    public boolean isMemberExist(String email, AuthProvider authProvider) {
        return memberRepository.findByEmailAndAuthProvider(email, authProvider).isPresent();
    }

    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getId();
    }

    @Transactional
    public boolean isNickNameExist(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    @Transactional
    public boolean saveMember(Member member) {
        // 처음 가입하는 이메일일 경우, db에 저장
        if(!isMemberExist(member.getEmail(), member.getAuthProvider())){
            memberRepository.save(member);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public AuthProvider transAuth(String oAuthProvider) {
        if(oAuthProvider.equals("kakao")) {
            return AuthProvider.KAKAO;
        } else if (oAuthProvider.equals("apple")) {
            return AuthProvider.APPLE;
        } else {
            return AuthProvider.ETC;
        }
    }

    @Transactional
    public boolean checkNickname(String nickname) {
        if (isNickNameExist(nickname)) {
            return false;
        } else {
            return true;
        }
    }

    // 선호 팀 수정
    public void updateTeams(String email, List<String> favoriteTeams) {
        Long memberId = getMemberId(email);

        String team1 = favoriteTeams.get(0);
        String team2 = null;
        String team3 = null;

        if(favoriteTeams.size() >= 2){
            team2 = favoriteTeams.get(1);
            if(favoriteTeams.size() >= 3) {
                team3 = favoriteTeams.get(2);
            }
        }
        memberRepository.updateTeams(memberId, team1, team2, team3);

    }

    public void updateNickname(String email, String nickname) {
        Long memberId = getMemberId(email);

        memberRepository.updateNickname(memberId, nickname);
    }

    public MypageDto getMypage(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);

        MypageDto mypage = new MypageDto();

        member.ifPresent(m -> {
            String nickname = m.getNickname();
            int point = m.getPoint();
            String team1 = m.getTeam1();
            String team2 = m.getTeam2();
            String team3 = m.getTeam3();

            mypage.setNickname(nickname);
            mypage.setGoalCount(point);

            List<MypageDto.FavoriteTeamsUrl> favoriteTeamsUrls = new ArrayList<>();

            favoriteTeamsUrls.addAll(convertName(team1));
            if (team2 != null) {
                favoriteTeamsUrls.addAll(convertName(team2));
            }
            if (team3 != null) {
                favoriteTeamsUrls.addAll(convertName(team3));
            }

            mypage.setFavoriteTeamsUrl(favoriteTeamsUrls);

        });


        return mypage;

    }

    public List<MypageDto.FavoriteTeamsUrl> convertName(String team) {
        List<MypageDto.FavoriteTeamsUrl> teamsUrlList = new ArrayList<>();
        teamsUrlList = squadRepository.getTeamInfo(teamNameConvertService.convertFromKrName(team));

        // 첫 번째 요소의 teamName 값을 변환하여 수정
        if (!teamsUrlList.isEmpty()) {  // team2List가 비어있지 않은 경우에만 실행
            MypageDto.FavoriteTeamsUrl firstTeam = teamsUrlList.get(0);

            // teamName을 한국어 이름으로 변환
            String name = teamNameConvertService.convertToKrName(firstTeam.getTeamName());
            firstTeam.setTeamName(name);  // 변환된 이름을 첫 번째 요소에 설정

            return teamsUrlList;
        } else {
            return null;
        }
    }
    // 사용자 평균 심박수
    public int getMemberAvgHeartRate(String email) {
        Long memberId = getMemberId(email);
        return memberRepository.getMemberAvgHeartRate(memberId);
    }

    // 사용자 포인트 즉시 업데이트
    @Transactional
    public void gainPoint(Member member, int point){
        member.setPoint(member.getPoint() + point);
        memberRepository.save(member);
        memberRepository.flush();
    }
}
