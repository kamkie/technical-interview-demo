package team.jit.technicalinterviewdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@SuppressWarnings("PMD.UseUtilityClass")
public class TechnicalInterviewDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechnicalInterviewDemoApplication.class, args);
    }
}
