package KickIt.server.domain.user.dto;

import KickIt.server.domain.user.OAuthInfoResponse;
import KickIt.server.domain.user.OAuthProvider;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

// https://openapi.naver.com/v1/nid/me 요청 결과값
// 엑세스 토큰 요청으로 반환 받는 사용자 정보
public class NaverInfoResponse implements OAuthInfoResponse {
    @JsonProperty("response")
    private Response response;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Response {
        private String email;
    }

    @Override
    public String getEmail() {
        return response.email;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.NAVER;
    }

}
