package KickIt.server.domain.user.service;

import KickIt.server.domain.user.dto.MemberDto;
import KickIt.server.domain.user.dto.MemberRepository;
import KickIt.server.domain.user.entity.Member;
import org.springframework.stereotype.Service;


@Service
public class MemberService {

    private final MemberRepository memberRepository;

    // 생성자 주입
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberDto findMemberById(Long memberId) {
        // Member 엔티티를 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 조회한 엔티티를 DTO로 변환하여 반환
        return new MemberDto(member);
    }
}
