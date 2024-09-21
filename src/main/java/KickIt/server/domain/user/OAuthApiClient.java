package KickIt.server.domain.user;

// 인가코드로 엑세스 토큰 요청, 엑세스 토큰으로 사용자 정보 요청
public interface OAuthApiClient {
    OAuthProvider oAuthProvider();
    String requestAccessToken(OAuthLoginParams params);
    OAuthInfoResponse requestOauthInfo(String accessToken);
}
