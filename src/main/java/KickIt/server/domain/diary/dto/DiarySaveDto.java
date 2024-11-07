package KickIt.server.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class DiarySaveDto {

    @JsonProperty("matchId")
    private Long fixtureId;

    private String teamName;
    private int emotion;
    private String diaryContext;
    private List<String> diaryPhotos;
    private String mom;
    private Boolean isPublic;

}