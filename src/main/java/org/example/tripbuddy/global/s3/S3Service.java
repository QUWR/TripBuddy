package org.example.tripbuddy.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            log.warn("File URL is null or empty, skipping deletion.");
            return;
        }
        try {
            // S3 객체 키는 버킷 이름 다음부터의 경로임
            // 예: https://bucket.s3.region.amazonaws.com/dir/filename.jpg -> dir/filename.jpg
            String fileKey = fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileKey));
            log.info("Successfully deleted S3 object: {}", fileUrl);
        } catch (Exception e) {
            log.error("Failed to delete S3 object for URL: {}. Error: {}", fileUrl, e.getMessage());
            // 예외를 다시 던지지 않아, 다른 이미지 삭제 작업에 영향을 주지 않음
        }
    }
}
