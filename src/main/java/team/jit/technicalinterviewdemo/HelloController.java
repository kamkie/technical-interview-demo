package team.jit.technicalinterviewdemo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        String payload = "Hello World!";
        return ResponseEntity.ok(payload);
    }
}
