package KickIt.server.domain.user;

import KickIt.server.domain.user.OAuthProvider;

// access token 요청 이후 반환 받는 값
public interface OAuthInfoResponse {
    String getEmail();

    String getName();

    OAuthProvider getOAuthProvider();
}
