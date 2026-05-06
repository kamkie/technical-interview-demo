package team.jit.technicalinterviewdemo.business.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.api.ApiProblemResponse;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;
import team.jit.technicalinterviewdemo.technical.security.SameSiteCsrfContract;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Public category reads with ADMIN-only category management.")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "List categories", description = "Public endpoint that returns categories ordered by name.")
    public ResponseEntity<List<CategoryResponse>> findAll() {
        List<CategoryResponse> payload = categoryService.findAll().stream().map(CategoryResponse::from).toList();
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    @Operation(
            summary = "Create a category", description = "Requires an authenticated session with the ADMIN role and a valid same-site CSRF header mirrored from the readable XSRF-TOKEN cookie.", security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    @Parameter(
            name = SameSiteCsrfContract.HEADER_NAME, in = ParameterIn.HEADER, required = true, description = "Same-site CSRF header whose value must match the readable XSRF-TOKEN cookie."
    )
    @ApiResponses({@ApiResponse(
            responseCode = "201", description = "Created", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryResponse.class)
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
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse payload = CategoryResponse.from(categoryService.create(request));
        return ResponseEntity.status(201).body(payload);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a category", description = "Requires an authenticated session with the ADMIN role and a valid same-site CSRF header mirrored from the readable XSRF-TOKEN cookie.", security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    @Parameter(
            name = SameSiteCsrfContract.HEADER_NAME, in = ParameterIn.HEADER, required = true, description = "Same-site CSRF header whose value must match the readable XSRF-TOKEN cookie."
    )
    @ApiResponses({@ApiResponse(
            responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CategoryResponse.class)
            )
    ), @ApiResponse(
            responseCode = "401", description = "Missing or invalid authenticated session.", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ApiProblemResponse.class)
            )
    ), @ApiResponse(
            responseCode = "403", description = "Authenticated user does not have the ADMIN role.", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ApiProblemResponse.class)
            )
    ), @ApiResponse(
            responseCode = "404", description = "Requested category was not found.", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ApiProblemResponse.class)
            )
    )
    })
    public ResponseEntity<CategoryResponse> update(
                                                   @PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest request
    ) {
        CategoryResponse payload = CategoryResponse.from(categoryService.update(id, request));
        return ResponseEntity.ok(payload);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a category", description = "Requires an authenticated session with the ADMIN role, a valid same-site CSRF header mirrored from the readable XSRF-TOKEN cookie, and a category that is not still assigned to books.", security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    @Parameter(
            name = SameSiteCsrfContract.HEADER_NAME, in = ParameterIn.HEADER, required = true, description = "Same-site CSRF header whose value must match the readable XSRF-TOKEN cookie."
    )
    @ApiResponses({@ApiResponse(responseCode = "204", description = "No Content"), @ApiResponse(
            responseCode = "401", description = "Missing or invalid authenticated session.", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ApiProblemResponse.class)
            )
    ), @ApiResponse(
            responseCode = "403", description = "Authenticated user does not have the ADMIN role.", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ApiProblemResponse.class)
            )
    ), @ApiResponse(
            responseCode = "404", description = "Requested category was not found.", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ApiProblemResponse.class)
            )
    ), @ApiResponse(
            responseCode = "409", description = "Category is still assigned to one or more books.", content = @Content(
                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ApiProblemResponse.class)
            )
    )
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
