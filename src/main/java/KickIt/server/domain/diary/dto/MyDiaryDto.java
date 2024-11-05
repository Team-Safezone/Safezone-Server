package KickIt.server.domain.diary.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MyDiaryDto {

    //diary table
    private Long diaryId;
    private String teamUrl;
    private String teamName;
    private Boolean isPublic;
    private String DiaryDate;
    private int emotion;
    private String mom;
    private int likes;

    // diaryPhoto table
    private List<String> diaryPhotos;

    // fixture table
    private String matchDate;
    private String homeTeamName;
    private int homeTeamScore;
    private String awayTeamName;
    private int awayTeamScore;

    // heartRateStatistics table
    private int highHeartRate;

    // DiaryLiked table
    private Boolean isLiked;

}
