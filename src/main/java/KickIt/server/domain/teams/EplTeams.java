package KickIt.server.domain.teams;

import java.util.HashMap;
import java.util.Map;

// 현재 상위 20 개 EPL 팀
public enum EplTeams {
    MCI("Manchester City", "맨시티", "맨체스터 시티"),
    ARS("Arsenal", "아스널", "아스널"),
    LIV("Liverpool", "리버풀", "리버풀"),
    AVL("Aston Villa", "애스턴 빌라", "애스턴 빌라"),
    TOT("Tottenham Hotspur", "토트넘", "토트넘 홋스퍼"),
    NEW("Newcastle United", "뉴캐슬", "뉴캐슬 유나이티드"),
    CHE("Chelsea", "첼시","첼시"),
    MUN("Manchester United", "맨유", "맨체스터 유나이티드"),
    WHU("West Ham United", "웨스트햄", "웨스트햄 유나이티드"),
    BHA("Brighton and Hove Albion", "브라이튼", "브라이튼 앤 호브 알비온"),
    BOU("AFC Bournemouth", "본머스", "AFC 본머스"),
    CRY("Crystal Palace", "크리스탈 팰리스", "크리스탈 팰리스"),
    WOL("Wolverhampton Wanderers", "울버햄튼", "울버햄튼 원더러스"),
    FUL("Fulham", "풀럼", "풀럼"),
    EVE("Everton", "에버턴", "에버턴"),
    BRE("Brentford", "브렌트포드", "브렌트포드"),
    NFO("Nottingham Forest", "노팅엄", "노팅엄 포레스트"),
    LUT("Luton Town", "루턴 타운", "루턴 타운"),
    BUR("Burnley", "번리", "번리"),
    SHU("Sheffield United", "셰필드", "셰필드 유나이티드")
    ;
    private final String engName;
    private final String krName;
    private final String krFullName;

    EplTeams(String engName, String krName, String krFullName){
        this.engName = engName;
        this.krName = krName;
        this.krFullName = krFullName;
    }
    // 한국어 이름으로 EplTeam 찾을 수 있도록 Map 사용
    private static final Map<String, EplTeams> BY_KRNAME = new HashMap<>();
    // 한국어 풀네임으로 EplTeam 찾을 수 있도록 Map 사용
    private static final Map<String, EplTeams> BY_KRFULLNAME = new HashMap<>();

    static {
        for(EplTeams e : values()){
            BY_KRNAME.put(e.krName, e);
            BY_KRFULLNAME.put(e.krFullName, e);
        }
    }

    // 한국어 이름으로 해당되는 EplTeam 찾아 반환
    public static EplTeams valueOfKrName(String krName){
        return BY_KRNAME.get(krName);
    }
    // 한국어 풀네임으로 해당되는 EplTeam 찾아 반환
    public static EplTeams valueOfKrFullName(String krFullName){
        return BY_KRFULLNAME.get(krFullName);
    }
}
