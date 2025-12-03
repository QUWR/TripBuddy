package org.example.tripbuddy.global.util;

import io.jsonwebtoken.ExpiredJwtException;
import org.example.tripbuddy.domain.user.domain.RoleType;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.domain.user.login.service.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil 단위 테스트")
class JwtUtilTest {

    // @InjectMocks를 사용하지 않고 직접 생성. 의존성이 하나뿐이라 간단함.
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(customUserDetailService);
        // 테스트용 시크릿 키와 만료 시간 설정 (실제 값과 달라도 됨)
        String testSecret = "c2VjcmV0S2V5LXRyaXBCdWRkeS1wcm9qZWN0LWphc3Blci1zcHJpbmctYm9vdC1qd3Qtc2VjcmV0LWtleQo="; // Base64 인코딩된 키
        ReflectionTestUtils.setField(jwtUtil, "secretKey", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpTime", 3600000L); // 1시간
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpTime", 86400000L); // 24시간
    }

    @Test
    @DisplayName("[성공] AccessToken 생성 후 파싱 시, 모든 클레임 정보가 일치한다")
    void createAndParseAccessToken_ShouldContainCorrectClaims() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .username("testuser")
                .nickname("tester")
                .role(RoleType.USER)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // when
        String token = jwtUtil.createAccessToken(userDetails);

        // then
        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.getEmail(token)).isEqualTo("test@example.com");
        assertThat(jwtUtil.getUsername(token)).isEqualTo("testuser");
        assertThat(jwtUtil.getNickname(token)).isEqualTo("tester");
        assertThat(jwtUtil.getRole(token)).isEqualTo(RoleType.USER);
        assertThat(jwtUtil.getClaims(token).get("category", String.class)).isEqualTo("access");
    }

    @Test
    @DisplayName("[실패] 만료된 토큰 검증 시 ExpiredJwtException 예외가 발생한다")
    void validateToken_ShouldThrowException_WhenTokenIsExpired() throws InterruptedException {
        // given
        // 만료 시간을 1ms로 설정하여 즉시 만료되는 토큰 생성
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpTime", 1L);
        User user = User.builder().email("test@example.com").build();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String expiredToken = jwtUtil.createAccessToken(userDetails);

        Thread.sleep(2); // 토큰이 확실히 만료되도록 잠시 대기

        // when & then
        assertThatThrownBy(() -> jwtUtil.validateToken(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
