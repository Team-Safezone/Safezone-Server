package KickIt.server.domain.diary.service;

import KickIt.server.aws.s3.service.S3Service;
import KickIt.server.domain.diary.entity.Diary;
import KickIt.server.domain.diary.entity.DiaryPhoto;
import KickIt.server.domain.diary.entity.DiaryPhotoRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiaryPhotoService {

    private final DiaryPhotoRepository diaryPhotoRepository;
    private final S3Service s3Service;

    @Autowired
    public DiaryPhotoService(DiaryPhotoRepository diaryPhotoRepository, S3Service s3Service) {
        this.diaryPhotoRepository = diaryPhotoRepository;
        this.s3Service = s3Service;
    }

    public DiaryPhoto photoSave(String s3Url, Diary diary) {
        DiaryPhoto diaryPhoto = new DiaryPhoto();
        diaryPhoto.setPhotoUrl(s3Url);
        diaryPhoto.setDiary(diary);
        return diaryPhotoRepository.save(diaryPhoto);
    }

    public void deleteDiaryPhoto(DiaryPhoto diaryPhoto) {
        // S3에서 파일 삭제
        String photoUrl = diaryPhoto.getPhotoUrl();
        String fileName = photoUrl.substring(photoUrl.lastIndexOf("/") + 1); // 파일 이름 추출

        // S3에서 파일 삭제
        s3Service.deleteFile(fileName);

        // 데이터베이스에서 DiaryPhoto 삭제
        diaryPhotoRepository.delete(diaryPhoto);
    }
}
