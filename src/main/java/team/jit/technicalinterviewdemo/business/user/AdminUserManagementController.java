package team.jit.technicalinterviewdemo.business.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.api.ApiProblemResponse;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;
import team.jit.technicalinterviewdemo.technical.security.SameSiteCsrfContract;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin Users", description = "ADMIN-only persisted user and role-management endpoints.")
public class AdminUserManagementController {

    private final AdminUserManagementService adminUserManagementService;

    public AdminUserManagementController(AdminUserManagementService adminUserManagementService) {
        this.adminUserManagementService = adminUserManagementService;
    }

    @GetMapping
    @Operation(
            summary = "List persisted users",
            description =
                    "Requires an authenticated session with the ADMIN role and returns persisted users together with"
                            + " current roles and role-grant provenance.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME))
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "OK",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                array =
                                        @ArraySchema(
                                                schema = @Schema(implementation = AdminUserAccountResponse.class)))),
        @ApiResponse(
                responseCode = "401",
                description = "Missing or invalid authenticated session.",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ApiProblemResponse.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Authenticated user does not have the ADMIN role.",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ApiProblemResponse.class)))
    })
    public ResponseEntity<List<AdminUserAccountResponse>> listUsers() {
        List<AdminUserAccountResponse> payload = adminUserManagementService.listUsers();
        return ResponseEntity.ok(payload);
    }

    @PutMapping("/{id}/roles")
    @Operation(
            summary = "Replace managed user roles",
            description =
                    "Requires an authenticated session with the ADMIN role, a valid same-site CSRF header mirrored"
                            + " from the readable XSRF-TOKEN cookie, and replaces the non-bootstrap role set for one"
                            + " persisted user.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME))
    @Parameter(
            name = SameSiteCsrfContract.HEADER_NAME,
            in = ParameterIn.HEADER,
            required = true,
            description = "Same-site CSRF header whose value must match the readable XSRF-TOKEN cookie.")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "OK",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = AdminUserAccountResponse.class))),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request payload.",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ApiProblemResponse.class))),
        @ApiResponse(
                responseCode = "401",
                description = "Missing or invalid authenticated session.",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ApiProblemResponse.class))),
        @ApiResponse(
                responseCode = "403",
                description = "Authenticated user does not have the ADMIN role.",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ApiProblemResponse.class))),
        @ApiResponse(
                responseCode = "404",
                description = "Persisted user was not found.",
                content =
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ApiProblemResponse.class)))
    })
    public ResponseEntity<AdminUserAccountResponse> replaceRoles(
            @PathVariable Long id, @Valid @RequestBody AdminUserRoleUpdateRequest request) {
        AdminUserAccountResponse payload = adminUserManagementService.replaceRoles(id, request);
        return ResponseEntity.ok(payload);
    }
}
