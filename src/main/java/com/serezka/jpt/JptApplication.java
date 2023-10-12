package com.serezka.jpt;

import com.serezka.jpt.api.GPTApi;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;


@SpringBootApplication
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JptApplication implements ApplicationRunner {
    GPTApi gpt;

    public static void main(String[] args) {
        SpringApplication.run(JptApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String r =gpt.query(List.of("2+2=5", "Почему?", "Сколько будет 2+3", "привет еще раз)", "сколько тебе лет?"), 0.7);
        System.out.println(r);
    }

}
