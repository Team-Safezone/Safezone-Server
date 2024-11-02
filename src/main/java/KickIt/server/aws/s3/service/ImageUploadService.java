package KickIt.server.aws.s3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.util.UUID;
import java.io.IOException;

// 클라이언트로 받은 이미지를 s3에 업로드
@Service
public class ImageUploadService {
    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    @Autowired
    public ImageUploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(String fileName) throws IOException {
        File file = new File("local/file/path/" + fileName); // 파일 이름을 바탕으로 로컬 경로에서 파일 로드
        String s3FileName = System.currentTimeMillis() + "_" + fileName; // 고유한 파일 이름 생성

        // S3에 파일 업로드 요청 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3FileName)
                .build();

        // 파일 업로드 실행
        s3Client.putObject(putObjectRequest, file.toPath());

        // 업로드된 파일의 S3 URL 반환
        return "https://" + bucket + ".s3.amazonaws.com/" + s3FileName;
    }

    public String changeFileName(String originalFileName) {
        String extension = "";  // 파일 확장자 추출
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);  // 확장자 포함
        }
        // UUID를 기반으로 파일명 생성
        return UUID.randomUUID().toString() + extension;
    }
}
