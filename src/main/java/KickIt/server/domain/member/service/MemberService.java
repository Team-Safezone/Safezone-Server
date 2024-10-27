package KickIt.server.domain.member.service;

import KickIt.server.domain.member.dto.MemberRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.AuthProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long getMemberId(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."))
                .getId();
    }

    @Transactional
    public boolean isMemberExist(String email, AuthProvider authProvider) {
        return memberRepository.findByEmailAndAuthProvider(email, authProvider).isPresent();
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
        if(oAuthProvider.equals("naver")) {
            return AuthProvider.NAVER;
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

    // 사용자 평균 심박수
    public int getMemberAvgHeartRate(String email) {
        Long memberId = getMemberId(email);
        return memberRepository.getMemberAvgHeartRate(memberId);
    }

}
