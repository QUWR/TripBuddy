package org.example.tripbuddy.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // private final StompJwtAuthInterceptor stompJwtAuthInterceptor; // 주석 처리된 JWT 인증 인터셉터

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독(sub) 요청의 prefix를 /sub로 설정
        registry.enableSimpleBroker("/sub");
        // 발행(pub) 요청의 prefix를 /pub로 설정
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") // WebSocket 연결 엔드포인트
                .setAllowedOriginPatterns(SecurityUrls.ALLOWED_ORIGINS.toArray(new String[0]))
                .withSockJS(); // SockJS 지원
    }

    /*
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // STOMP 메시지 처리 전, JWT 토큰을 검증하는 인터셉터를 등록합니다.
        // 이 인터셉터는 STOMP CONNECT 메시지의 헤더에서 'Authorization' 토큰을 추출하여
        // 유효성을 검증하고, 인증된 사용자의 정보를 SecurityContext에 설정합니다.
        // 이를 통해 @AuthenticationPrincipal과 같은 어노테이션을 WebSocket 컨트롤러에서도 사용할 수 있게 됩니다.
        registration.interceptors(stompJwtAuthInterceptor);
    }
    */
}
