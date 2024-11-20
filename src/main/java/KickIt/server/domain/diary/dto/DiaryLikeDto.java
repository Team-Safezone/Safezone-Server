package KickIt.server.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DiaryLikeDto {

    @JsonProperty("isLiked")
    private Boolean isLiked;

}
