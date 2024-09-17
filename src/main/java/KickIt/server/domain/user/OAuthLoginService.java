package KickIt.server.domain.user;

import KickIt.server.domain.user.dto.MemberRepository;
import KickIt.server.domain.user.dto.NaverLoginParams;
import KickIt.server.domain.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public AuthTokens login(OAuthLoginParams params) {
        try {
            // 로그인 처리 로직
            OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
            Long memberId = findOrCreateMember(oAuthInfoResponse);
            return authTokensGenerator.generate(memberId);
        } catch (Exception e) {
            if (params instanceof NaverLoginParams) {
                NaverLoginParams naverParams = (NaverLoginParams) params;
                System.out.println("Authorization Code: " + naverParams.getAuthorizationCode());
                System.out.println("State: " + naverParams.getState());
            }
            System.out.println("Error during login process" + e);  // 예외 로그 추가
            throw e;
        }
    }

    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return memberRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(Member::getMemberId)
                .orElseGet(() -> newMember(oAuthInfoResponse));
    }

    private Long newMember(OAuthInfoResponse oAuthInfoResponse) {
        Member member = Member.builder()
                .email(oAuthInfoResponse.getEmail())
                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                .build();

        return memberRepository.save(member).getMemberId();
    }
}