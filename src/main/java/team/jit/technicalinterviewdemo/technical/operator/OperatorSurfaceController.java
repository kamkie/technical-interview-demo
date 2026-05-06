package team.jit.technicalinterviewdemo.technical.operator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.api.ApiProblemResponse;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Operator", description = "ADMIN-only operational inspection surface.")
public class OperatorSurfaceController {

    private final OperatorSurfaceService operatorSurfaceService;

    @GetMapping("/operator-surface")
    @Operation(
        summary = "Get operator inspection surface", description = "Requires an authenticated session with the ADMIN role and returns audit, runtime, and operational visibility in one payload.", security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    @ApiResponses({@ApiResponse(
        responseCode = "200", description = "OK", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = OperatorSurfaceResponse.class)
        )
    ), @ApiResponse(
        responseCode = "401", description = "Missing or invalid authenticated session.", content = @Content(
            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ApiProblemResponse.class)
        )
    ), @ApiResponse(
        responseCode = "403", description = "Authenticated user does not have the ADMIN role.", content = @Content(
            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ApiProblemResponse.class)
        )
    )
    })
    public ResponseEntity<OperatorSurfaceResponse> getSurface() {
        OperatorSurfaceResponse payload = operatorSurfaceService.getOperatorSurface();
        return ResponseEntity.ok(payload);
    }
}
