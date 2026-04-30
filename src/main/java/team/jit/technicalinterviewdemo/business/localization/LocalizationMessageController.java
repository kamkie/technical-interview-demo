package team.jit.technicalinterviewdemo.business.localization;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;

@Validated
@RestController
@RequestMapping("/api/localization-messages")
@Tag(name = "Localization Messages", description = "Public localization lookups with ADMIN-only management operations.")
public class LocalizationMessageController {

    private final LocalizationMessageService localizationMessageService;

    public LocalizationMessageController(LocalizationMessageService localizationMessageService) {
        this.localizationMessageService = localizationMessageService;
    }

    @GetMapping
    @Operation(summary = "List localization messages", description = "Public endpoint with pageable localization-message results.")
    public ResponseEntity<Page<LocalizationMessageResponse>> findAll(
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<LocalizationMessageResponse> payload = localizationMessageService.findAll(pageable)
                .map(LocalizationMessageResponse::from);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a localization message by id", description = "Public endpoint for a single localization message.")
    public ResponseEntity<LocalizationMessageResponse> findById(@PathVariable Long id) {
        LocalizationMessageResponse payload = LocalizationMessageResponse.from(localizationMessageService.findById(id));
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/key/{messageKey}/lang/{language}")
    @Operation(summary = "Lookup a localization message by key and language", description = "Public exact-match lookup.")
    public ResponseEntity<LocalizationMessageResponse> findByMessageKeyAndLanguage(
            @PathVariable String messageKey,
            @PathVariable @Pattern(regexp = "^[a-zA-Z]{2}$", message = "language must be a two-letter ISO 639-1 code") String language
    ) {
        LocalizationMessageResponse payload =
                LocalizationMessageResponse.from(localizationMessageService.findByMessageKeyAndLanguage(messageKey, language));
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    @Operation(
            summary = "Create a localization message",
            description = "Requires an authenticated session with the ADMIN role.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<LocalizationMessageResponse> create(@Valid @RequestBody LocalizationMessageRequest request) {
        LocalizationMessageResponse payload = LocalizationMessageResponse.from(localizationMessageService.create(request));
        return ResponseEntity.status(201).body(payload);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a localization message",
            description = "Requires an authenticated session with the ADMIN role.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<LocalizationMessageResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LocalizationMessageRequest request
    ) {
        LocalizationMessageResponse payload = LocalizationMessageResponse.from(localizationMessageService.update(id, request));
        return ResponseEntity.ok(payload);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a localization message",
            description = "Requires an authenticated session with the ADMIN role.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        localizationMessageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/language/{language}")
    @Operation(summary = "List localization messages for one language", description = "Public endpoint ordered by message key.")
    public ResponseEntity<List<LocalizationMessageResponse>> findAllByLanguage(
            @PathVariable @Pattern(regexp = "^[a-zA-Z]{2}$", message = "language must be a two-letter ISO 639-1 code") String language
    ) {
        List<LocalizationMessageResponse> payload = localizationMessageService.findAllByLanguage(language).stream()
                .map(LocalizationMessageResponse::from)
                .toList();
        return ResponseEntity.ok(payload);
    }
}
