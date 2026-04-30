package team.jit.technicalinterviewdemo.technical;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Technical", description = "Public technical overview and smoke-test endpoints.")
public class HelloController {

    private final TechnicalOverviewService technicalOverviewService;

    @GetMapping("/")
    @Operation(
            summary = "Return technical application overview",
            description = "Public endpoint that combines build, git, dependency, runtime, and important configuration details."
    )
    public ResponseEntity<TechnicalOverviewResponse> overview() {
        TechnicalOverviewResponse payload = technicalOverviewService.getOverview();
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/hello")
    @Operation(summary = "Return Hello World", description = "Public endpoint for quick smoke testing.")
    public ResponseEntity<String> hello() {
        String payload = "Hello World!";
        return ResponseEntity.ok(payload);
    }
}
