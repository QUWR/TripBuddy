package org.example.tripbuddy.domain.content.domain;

public enum ContentType {

    TIP("팁"),
    REVIEW("리뷰");

    private final String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }
}
