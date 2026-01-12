package org.example.tripbuddy.domain.test;

import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//coderabbit 시험용 추후 삭제
@RestController
public class BadController {
    @Autowired // [위반 1] 필드 주입 금지
    private UserRepository userRepository;

    @GetMapping("/bad")
    public User badApi() { // [위반 2] Entity 직접 반환 금지
        return new User();
    }
}
