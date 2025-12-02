package org.example.tripbuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class TripbuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripbuddyApplication.class, args);
    }

}
