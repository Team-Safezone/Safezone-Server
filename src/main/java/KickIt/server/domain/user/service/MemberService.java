package KickIt.server.domain.user.service;

import KickIt.server.domain.user.dto.MemberRepository;
import KickIt.server.domain.user.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public boolean isExist(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public boolean saveMember(Member member) {
        // 처음 가입하는 이메일일 경우, db에 저장
        if(!isExist(member.getEmail())){
            memberRepository.save(member);
            return true;
        } else {
            return false;
        }
    }

}
