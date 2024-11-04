package KickIt.server.aws.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

// 클라이언트로 받은 이미지를 s3에 업로드
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFileFromUrl(String fileUrl) throws IOException {
        // 파일 확장자 추출
        String fileExtension = getFileExtension(fileUrl);

        // URL로부터 파일 다운로드
        File tempFile = downloadFileFromUrl(fileUrl, fileExtension); // 확장자 전달

        // UUID와 확장자를 결합하여 파일 이름 생성
        String fileName = UUID.randomUUID() + fileExtension;

        // S3에 업로드
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, tempFile));

        tempFile.delete(); // 임시 파일 삭제
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private File downloadFileFromUrl(String fileUrl, String extension) throws IOException {
        URL url = new URL(fileUrl);
        // 임시 파일 생성 시 확장자를 포함
        Path tempFilePath = Files.createTempFile("temp_", extension);
        File tempFile = tempFilePath.toFile();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(url.openStream().readAllBytes());
        }

        return tempFile;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    private String getFileExtension(String fileUrl) {
        // URL의 마지막 부분에서 파일 이름과 확장자 추출
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex); // ".jpg" 또는 ".png"
        }
        return ""; // 확장자가 없는 경우
    }
}
