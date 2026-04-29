package team.jit.technicalinterviewdemo;

import org.springframework.boot.SpringApplication;

public class TestTechnicalInterviewDemoApplication {

    public static void main(String[] args) {
        SpringApplication.from(TechnicalInterviewDemoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
