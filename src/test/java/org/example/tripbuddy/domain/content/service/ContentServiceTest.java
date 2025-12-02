package org.example.tripbuddy.domain.content.service;

import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.domain.ContentType;
import org.example.tripbuddy.domain.content.dto.ContentUploadRequest;
import org.example.tripbuddy.domain.content.repository.ContentRepository;
import org.example.tripbuddy.domain.image.domain.ImageMetadata;
import org.example.tripbuddy.domain.image.domain.ImageStatus;
import org.example.tripbuddy.domain.image.repository.ImageMetadataRepository;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContentService 단위 테스트")
class ContentServiceTest {

    @InjectMocks
    private ContentService contentService;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private ImageMetadataRepository imageMetadataRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("[작성] 게시글 저장 시, 본문에 포함된 이미지의 상태가 TEMP에서 ACTIVE로 변경된다")
    void uploadContent_ShouldChangeImageStatusToActive_WhenContentIsSaved() {
        // given
        String imageUrl = "https://s3.bucket/temp-image.jpg";
        User user = User.builder().id(1L).build();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        ContentUploadRequest request = new ContentUploadRequest("Test Title", "![img](" + imageUrl + ")");
        ImageMetadata tempImage = ImageMetadata.builder().url(imageUrl).status(ImageStatus.TEMP).build();

        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(contentRepository.save(any(Content.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(imageMetadataRepository.findByUrlIn(any())).willReturn(List.of(tempImage));

        // when
        contentService.uploadContent(ContentType.REVIEW, request, userDetails);

        // then
        assertThat(tempImage.getStatus()).isEqualTo(ImageStatus.ACTIVE);
        assertThat(tempImage.getContent()).isNotNull();
    }

    @Nested
    @DisplayName("게시글 수정 (updateContent) 테스트")
    class UpdateContentTests {
        @Test
        @DisplayName("[성공] 이미지가 교체되면, 기존 이미지는 TEMP로, 새 이미지는 ACTIVE로 상태가 변경된다")
        void updateContent_ShouldChangeImageStatus_WhenImagesAreModified() {
            // given
            long authorId = 1L;
            long contentId = 10L;
            String oldImageUrl = "https://s3.bucket/old-image.jpg";
            String newImageUrl = "https://s3.bucket/new-image.jpg";

            User author = User.builder().id(authorId).build();
            CustomUserDetails userDetails = new CustomUserDetails(author);
            Content content = Content.builder().id(contentId).user(author).title("Old").body("![](" + oldImageUrl + ")").build();
            ImageMetadata oldImage = ImageMetadata.builder().url(oldImageUrl).status(ImageStatus.ACTIVE).content(content).build();
            ImageMetadata newImage = ImageMetadata.builder().url(newImageUrl).status(ImageStatus.TEMP).build();
            ContentUploadRequest request = new ContentUploadRequest("New", "![](" + newImageUrl + ")");

            given(contentRepository.findById(contentId)).willReturn(Optional.of(content));
            given(imageMetadataRepository.findByContentId(contentId)).willReturn(List.of(oldImage));
            given(imageMetadataRepository.findByUrlIn(any())).willReturn(List.of(newImage));

            // when
            contentService.updateContent(contentId, request, userDetails);

            // then
            assertThat(oldImage.getStatus()).isEqualTo(ImageStatus.TEMP);
            assertThat(oldImage.getContent()).isNull();
            assertThat(newImage.getStatus()).isEqualTo(ImageStatus.ACTIVE);
            assertThat(newImage.getContent()).isEqualTo(content);
            assertThat(content.getTitle()).isEqualTo("New");
        }

        @Test
        @DisplayName("[실패] 작성자가 아닌 유저가 수정을 시도하면 FORBIDDEN_ACCESS 예외가 발생한다")
        void updateContent_ShouldThrowException_WhenUserIsNotAuthor() {
            // given
            User author = User.builder().id(1L).build();
            User otherUser = User.builder().id(2L).build();
            CustomUserDetails otherUserDetails = new CustomUserDetails(otherUser);
            Content content = Content.builder().id(10L).user(author).build();
            ContentUploadRequest request = new ContentUploadRequest("New", "New");

            given(contentRepository.findById(10L)).willReturn(Optional.of(content));

            // when & then
            assertThatThrownBy(() -> contentService.updateContent(10L, request, otherUserDetails))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS);
        }
    }

    @Nested
    @DisplayName("게시글 삭제 (deleteContent) 테스트")
    class DeleteContentTests {
        @Test
        @DisplayName("[성공] 게시글 삭제 시, 포함된 이미지들은 TEMP 상태로 변경된다 (Soft Delete)")
        void deleteContent_ShouldSoftDeleteImages_WhenContentIsDeleted() {
            // given
            User author = User.builder().id(1L).build();
            CustomUserDetails userDetails = new CustomUserDetails(author);
            Content content = Content.builder().id(10L).user(author).build();
            ImageMetadata image1 = ImageMetadata.builder().url("url1").status(ImageStatus.ACTIVE).content(content).build();
            ImageMetadata image2 = ImageMetadata.builder().url("url2").status(ImageStatus.ACTIVE).content(content).build();

            given(contentRepository.findById(10L)).willReturn(Optional.of(content));
            given(imageMetadataRepository.findByContentId(10L)).willReturn(List.of(image1, image2));

            // when
            contentService.deleteContent(10L, userDetails);

            // then
            assertThat(image1.getStatus()).isEqualTo(ImageStatus.TEMP);
            assertThat(image1.getContent()).isNull();
            assertThat(image2.getStatus()).isEqualTo(ImageStatus.TEMP);
            assertThat(image2.getContent()).isNull();
            verify(contentRepository).delete(content);
        }
    }
}
