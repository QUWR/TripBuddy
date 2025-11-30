package org.example.tripbuddy.global.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException | SdkClientException e) {
            log.error("Error uploading file to S3", e);
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            log.warn("File URL is null or empty, skipping deletion.");
            return;
        }
        try {
            String fileKey = fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileKey));
            log.info("Successfully deleted S3 object: {}", fileUrl);
        } catch (Exception e) {
            log.error("Failed to delete S3 object for URL: {}. Error: {}", fileUrl, e.getMessage());
        }
    }
}
