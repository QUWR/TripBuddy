package org.example.tripbuddy.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 86400) // 1일 (초 단위)
public class RefreshToken {

    @Id
    private String token; // Refresh Token 값 자체가 ID가 됨

    @Indexed // 조회 조건으로 사용하기 위해 인덱싱
    private String email;

    private String role;
}
