package team.jit.technicalinterviewdemo.business.localization;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;

@Validated
@RestController
@RequestMapping("/api/localizations")
@Tag(name = "Localizations", description = "Public localization reads with ADMIN-only management operations.")
public class LocalizationController {

    private final LocalizationService localizationService;

    public LocalizationController(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

    @GetMapping
    @Operation(
            summary = "List localizations",
            description = "Public endpoint with pageable results. Optional messageKey and language filters narrow the collection."
    )
    public ResponseEntity<Page<LocalizationResponse>> findAll(
            @RequestParam(required = false) String messageKey,
            @RequestParam(required = false)
            @Pattern(regexp = "^[a-zA-Z]{2}$", message = "language must be a two-letter ISO 639-1 code") String language,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<LocalizationResponse> payload = localizationService.findAll(pageable, messageKey, language)
                .map(LocalizationResponse::from);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a localization by id", description = "Public endpoint for a single localization.")
    public ResponseEntity<LocalizationResponse> findById(@PathVariable Long id) {
        LocalizationResponse payload = LocalizationResponse.from(localizationService.findById(id));
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    @Operation(
            summary = "Create a localization",
            description = "Requires an authenticated session with the ADMIN role.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<LocalizationResponse> create(@Valid @RequestBody LocalizationRequest request) {
        LocalizationResponse payload = LocalizationResponse.from(localizationService.create(request));
        return ResponseEntity.status(201).body(payload);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a localization",
            description = "Requires an authenticated session with the ADMIN role.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<LocalizationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LocalizationRequest request
    ) {
        LocalizationResponse payload = LocalizationResponse.from(localizationService.update(id, request));
        return ResponseEntity.ok(payload);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a localization",
            description = "Requires an authenticated session with the ADMIN role.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        localizationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
