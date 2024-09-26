package KickIt.server.domain.user.service;

import KickIt.server.domain.user.dto.MemberRepository;
import KickIt.server.domain.user.entity.Member;
import KickIt.server.domain.user.entity.OAuthProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public boolean isMemberExist(String email, OAuthProvider oAuthProvider) {
        return memberRepository.findByEmailAndOAuthProvider(email, oAuthProvider).isPresent();
    }

    @Transactional
    public boolean saveMember(Member member) {
        // 처음 가입하는 이메일일 경우, db에 저장
        if(!isMemberExist(member.getEmail(), member.getOAuthProvider())){
            memberRepository.save(member);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public OAuthProvider transAuth(String oAuthProvider) {
        if(oAuthProvider.equals("naver")) {
            return OAuthProvider.NAVER;
        } else if (oAuthProvider.equals("apple")) {
            return OAuthProvider.APPLE;
        } else {
            return OAuthProvider.ETC;
        }
    }


}
