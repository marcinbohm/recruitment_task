package io.getint.recruitment_task;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class SynchronizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynchronizerApplication.class, args);
    }
}
