package KickIt.server.domain.member.service;

import KickIt.server.domain.member.dto.MemberRepository;
import KickIt.server.domain.member.entity.Member;
import KickIt.server.domain.member.entity.AuthProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

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

}
