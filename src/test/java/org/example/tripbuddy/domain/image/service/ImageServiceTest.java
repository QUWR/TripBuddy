package org.example.tripbuddy.domain.image.service;

import org.example.tripbuddy.domain.image.domain.ImageMetadata;
import org.example.tripbuddy.domain.image.domain.ImageStatus;
import org.example.tripbuddy.domain.image.repository.ImageMetadataRepository;
import org.example.tripbuddy.global.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageService 단위 테스트")
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageMetadataRepository imageMetadataRepository;

    @Test
    @DisplayName("[성공] 이미지 업로드 시, S3 URL을 받아 TEMP 상태의 메타데이터를 저장한다")
    void uploadImage_ShouldSaveTempMetadata_WhenImageIsUploaded() {
        // given
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test-image".getBytes());
        String fakeS3Url = "https://s3.bucket/test.jpg";

        given(s3Service.upload(any(), anyString())).willReturn(fakeS3Url);

        // when
        String returnedUrl = imageService.uploadImage(imageFile);

        // then
        ArgumentCaptor<ImageMetadata> metadataCaptor = ArgumentCaptor.forClass(ImageMetadata.class);
        verify(imageMetadataRepository).save(metadataCaptor.capture());
        ImageMetadata savedMetadata = metadataCaptor.getValue();

        assertThat(returnedUrl).isEqualTo(fakeS3Url);
        assertThat(savedMetadata.getUrl()).isEqualTo(fakeS3Url);
        assertThat(savedMetadata.getStatus()).isEqualTo(ImageStatus.TEMP);
        assertThat(savedMetadata.getContent()).isNull();
    }
}
