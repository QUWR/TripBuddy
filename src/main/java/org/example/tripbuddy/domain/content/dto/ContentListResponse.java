package org.example.tripbuddy.domain.content.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ContentListResponse {

    private String title;
    private String username;
    private double rateAvg;
    private LocalDateTime createdAt;

}
