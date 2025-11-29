package org.example.tripbuddy.global.config;

import org.example.tripbuddy.domain.content.domain.ContentType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToContentTypeConverter implements Converter<String, ContentType>{


    /**
     * String 타입을 contentType로 바꿔주는 매소드
     * @param source
     * @return ContentType
     */
    @Override
    public ContentType convert(String source) {
        if (source == null) {
            return null;
        }
        try {
            // ContentType Enum의 valueOf 메소드로 일치하는 Enum을 찾음
            return ContentType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            // "tip"이나 "review"가 아닌 "hello" 같은 잘못된 값이 들어오면 IllegalArgumentException이 발생
            // 이때 null을 반환하면 Spring이 알아서 400 Bad Request 오류를 처리
            return null;
        }
    }
}
