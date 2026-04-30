package team.jit.technicalinterviewdemo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Hello", description = "Simple public smoke-test endpoint.")
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "Return Hello World", description = "Public endpoint for quick smoke testing.")
    public ResponseEntity<String> hello() {
        String payload = "Hello World!";
        return ResponseEntity.ok(payload);
    }
}
