package org.example.tripbuddy.domain.content.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.domain.ContentType;
import org.example.tripbuddy.domain.content.dto.ContentListResponse;
import org.example.tripbuddy.domain.content.repository.ContentRepository;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    /**
     * content 종류별로 리스트 가져오기
     */
    @Transactional(readOnly = true)
    public List<ContentListResponse> getContentList(ContentType contentType){
        List<Content> contents = contentRepository.findByContentType(contentType);

        return contents.stream()
                .map(content -> new ContentListResponse(
                        content.getTitle(),
                        content.getUser().getUsername(), // User 엔티티에서 username 가져오기
                        content.getRateAvg(),
                        content.getCreatedAt()
                ))
                .toList();
    }

}
