package KickIt.server.aws.s3.service;

import KickIt.server.domain.diary.entity.DiaryPhoto;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;

// 클라이언트로 받은 이미지를 s3에 업로드
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.credentials.secretKey}")
    private String key;

    @Autowired
    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    // 파일 업로드
    public String  uploadFileFromUrl(MultipartFile file) throws IOException {
        // 확장자 추출
        String fileExtension = getFileExtension(file.getOriginalFilename());
        System.out.println("fileExtension = " + fileExtension);
        if (fileExtension.isEmpty()) {
            fileExtension = ".jpg"; // 기본 확장자 설정
        }

        String fileName = UUID.randomUUID() + fileExtension;

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();

        // Content-Type 강제 설정 (확장자 기반)
        String contentType = resolveContentType(fileExtension);
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());

        System.out.println("Resolved contentType = " + contentType);

        // Content-Disposition 설정
        metadata.addUserMetadata("Content-Disposition", "inline");

        // S3 업로드 요청
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata);
        amazonS3.putObject(putObjectRequest);

        // 업로드된 파일의 URL 반환
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    // s3 데이터 삭제
    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    // 확장자 설정
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex); // ".jpg" 또는 ".png"
        }
        return ""; // 확장자가 없는 경우
    }

    private String resolveContentType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            default:
                return "application/octet-stream"; // 기본값
        }
        }
    /*
    // S3에서 이미지를 가져와 Base64로 변환
    public String getImageAsBase64() throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, key);
        InputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        byte[] imageBytes = outputStream.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String contentType = s3Object.getObjectMetadata().getContentType();
        return "data:" + contentType + ";base64," + base64Image;
    }

     */

}
