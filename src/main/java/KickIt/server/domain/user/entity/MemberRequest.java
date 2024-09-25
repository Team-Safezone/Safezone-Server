package KickIt.server.domain.user.entity;

import lombok.Getter;

import java.util.List;

@Getter
public class MemberRequest {
    private String email;
    private String nickname;
    private List<String> favoriteTeams;
    private boolean marketingConsent;
}
