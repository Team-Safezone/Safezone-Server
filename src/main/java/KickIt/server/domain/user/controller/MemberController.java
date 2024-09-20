package KickIt.server.domain.user.controller;

import KickIt.server.domain.user.JwtTokenProvider;
import KickIt.server.domain.user.dto.MemberDto;
import KickIt.server.domain.user.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider; // JWT 검증을 위한 클래스

    // 생성자 주입
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/api/member")
    public ResponseEntity<MemberDto> getMember(@RequestHeader("accessToken") String accessToken) {
        // Authorization 헤더에서 Bearer 토큰 추출
        String token = accessToken.replace("Bearer ", "");

        // 토큰에서 사용자 ID 추출
        Long memberId = jwtTokenProvider.getUserIdFromToken(token);

        // 사용자 정보 조회
        MemberDto memberDto = memberService.findMemberById(memberId);

        return ResponseEntity.ok(memberDto);
    }

}
