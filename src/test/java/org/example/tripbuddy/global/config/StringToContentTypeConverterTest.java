package org.example.tripbuddy.global.config;

import org.example.tripbuddy.domain.content.domain.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StringToContentTypeConverter 단위 테스트")
class StringToContentTypeConverterTest {

    private final StringToContentTypeConverter converter = new StringToContentTypeConverter();

    @Test
    @DisplayName("[성공] 소문자 'tip' 입력 시 ContentType.TIP으로 변환된다")
    void convert_ShouldReturnCorrectEnum_WhenInputIsLowercase() {
        // given
        String source = "tip";

        // when
        ContentType result = converter.convert(source);

        // then
        assertThat(result).isEqualTo(ContentType.TIP);
    }

    @Test
    @DisplayName("[성공] 대문자 'REVIEW' 입력 시 ContentType.REVIEW로 변환된다")
    void convert_ShouldReturnCorrectEnum_WhenInputIsUppercase() {
        // given
        String source = "REVIEW";

        // when
        ContentType result = converter.convert(source);

        // then
        assertThat(result).isEqualTo(ContentType.REVIEW);
    }

    @Test
    @DisplayName("[실패] 잘못된 문자열 입력 시 null을 반환한다")
    void convert_ShouldReturnNull_WhenInputIsInvalid() {
        // given
        String source = "invalid_type";

        // when
        ContentType result = converter.convert(source);

        // then
        assertThat(result).isNull();
    }
}
