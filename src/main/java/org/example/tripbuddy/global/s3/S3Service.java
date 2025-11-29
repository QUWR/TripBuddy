package org.example.tripbuddy.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        // 파일 이름의 중복을 피하기 위해 UUID 사용
        String fileName = dirName + "/" + UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // S3에 파일 업로드
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)); // PublicRead 권한으로 업로드

        // 업로드된 파일의 URL 반환
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void delete(String fileUrl) {
        // URL에서 파일 키(경로)를 디코딩하여 추출
        String fileKey = URLDecoder.decode(fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1), StandardCharsets.UTF_8);
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileKey));
    }
}
