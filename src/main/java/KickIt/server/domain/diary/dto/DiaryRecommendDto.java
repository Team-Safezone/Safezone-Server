package KickIt.server.domain.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DiaryRecommendDto {

    // diary table
    private Long diaryId;
    private int grade;
    private String teamUrl;
    private String teamName;
    private String diaryContent;
    private String diaryDate;
    private int emotion;
    private String mom;
    private Boolean isLiked;

    // member table
    private String nickname;

    // fixture table
    private String matchDate;
    private String homeTeamName;
    private Integer homeTeamScore;
    private String awayTeamName;
    private Integer awayTeamScore;

    // heartRateStatistics table
    private int highHeartRate;

    // diaryPhoto table
    private List<String> diaryPhotos;

    // diaryLiked table
    private int likes;
}
