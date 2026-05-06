package team.jit.technicalinterviewdemo.technical.info;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Hidden
@Tag(name = "Technical", description = "Internal technical overview and smoke-test endpoints.")
public class TechnicalOverviewController {

    private final TechnicalOverviewService technicalOverviewService;

    @GetMapping("/")
    @Operation(
            summary = "Return technical application overview", description = "Internal endpoint that combines build, git, dependency, runtime, and important configuration details."
    )
    public ResponseEntity<TechnicalOverviewResponse> overview() {
        TechnicalOverviewResponse payload = technicalOverviewService.getOverview();
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/hello")
    @Operation(summary = "Return Hello World", description = "Internal endpoint for quick smoke testing.")
    public ResponseEntity<String> hello() {
        String payload = "Hello World!";
        return ResponseEntity.ok(payload);
    }
}
