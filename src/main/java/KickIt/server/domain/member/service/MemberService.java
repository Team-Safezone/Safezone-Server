package KickIt.server.domain.member.service;

import KickIt.server.domain.member.entity.MemberRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.AuthProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

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
}
