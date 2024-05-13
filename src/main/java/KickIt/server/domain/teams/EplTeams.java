package KickIt.server.domain.teams;

import java.util.HashMap;
import java.util.Map;

// 현재 상위 20 개 EPL 팀
public enum EplTeams {
    MCI("Manchester City", "맨시티"),
    ARS("Arsenal", "아스널"),
    LIV("Liverpool", "리버풀"),
    AVL("Aston Villa", "애스턴 빌라"),
    TOT("Tottenham Hotspur", "토트넘"),
    NEW("Newcastle United", "뉴캐슬"),
    CHE("Chelsea", "첼시"),
    MUN("Manchester United", "맨유"),
    WHU("West Ham United", "웨스트햄"),
    BHA("Brighton and Hove Albion", "브라이튼"),
    BOU("AFC Bournemouth", "본머스"),
    CRY("Crystal Palace", "크리스탈 팰리스"),
    WOL("Wolverhampton Wanderers", "울버햄튼"),
    FUL("Fulham", "풀럼"),
    EVE("Everton", "에버턴"),
    BRE("Brentford", "브렌트포드"),
    NFO("Nottingham Forest", "노팅엄"),
    LUT("Luton Town", "루턴 타운"),
    BUR("Burnley", "번리"),
    SHU("Sheffield United", "셰필드")
    ;
    private final String engName;
    private final String krName;

    EplTeams(String engName, String krName){
        this.engName = engName;
        this.krName = krName;
    }
    // 한국어 이름으로 EPLTEAM 찾을 수 있도록 MAP 사용
    private static final Map<String, EplTeams> BY_KRNAME = new HashMap<>();
    static {
        for(EplTeams e : values()){
            BY_KRNAME.put(e.krName, e);
        }
    }
    // 한국어 이름으로 해당 되는 EplTeam 찾아 반환
    public static EplTeams valueOfKrName(String krName){
        return BY_KRNAME.get(krName);
    }
}
