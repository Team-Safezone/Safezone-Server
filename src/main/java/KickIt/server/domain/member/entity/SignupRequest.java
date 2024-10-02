package KickIt.server.domain.member.entity;

import lombok.Getter;

import java.util.List;

@Getter
public class SignupRequest {
    private String email;
    private String nickname;
    private List<String> favoriteTeams;
    private boolean marketingConsent;
}
