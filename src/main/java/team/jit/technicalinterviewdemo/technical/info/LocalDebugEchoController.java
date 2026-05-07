package team.jit.technicalinterviewdemo.technical.info;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("local")
@Hidden
@Tag(name = "Technical", description = "Local-only technical helper endpoints.")
public class LocalDebugEchoController {

    @PostMapping(value = "/debug/echo", consumes = "*/*", produces = "*/*")
    @Operation(
            summary = "Echo the request body",
            description = "Local-only endpoint for IntelliJ HTTP Client report redirection.")
    public ResponseEntity<String> echo(@RequestBody String body) {
        return ResponseEntity.ok(body);
    }
}
