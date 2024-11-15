package KickIt.server.domain.diary.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class DiaryEditDto {

    private String teamName;
    private int emotion;
    private String diaryContent;
    private String mom;
    private boolean isPublic;

    private List<MultipartFile> diaryPhotos;


}
