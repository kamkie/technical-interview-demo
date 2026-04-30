package team.jit.technicalinterviewdemo.business.localization;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/localization-messages")
public class LocalizationMessageController {

    private final LocalizationMessageService localizationMessageService;

    public LocalizationMessageController(LocalizationMessageService localizationMessageService) {
        this.localizationMessageService = localizationMessageService;
    }

    @GetMapping
    public ResponseEntity<Page<LocalizationMessageResponse>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<LocalizationMessageResponse> payload = localizationMessageService.findAll(pageable)
                .map(LocalizationMessageResponse::from);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalizationMessageResponse> findById(@PathVariable Long id) {
        LocalizationMessageResponse payload = LocalizationMessageResponse.from(localizationMessageService.findById(id));
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/key/{messageKey}/lang/{language}")
    public ResponseEntity<LocalizationMessageResponse> findByMessageKeyAndLanguage(
            @PathVariable String messageKey,
            @PathVariable @Pattern(regexp = "^[a-zA-Z]{2}$", message = "language must be a two-letter ISO 639-1 code") String language
    ) {
        LocalizationMessageResponse payload =
                LocalizationMessageResponse.from(localizationMessageService.findByMessageKeyAndLanguage(messageKey, language));
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    public ResponseEntity<LocalizationMessageResponse> create(@Valid @RequestBody LocalizationMessageRequest request) {
        LocalizationMessageResponse payload = LocalizationMessageResponse.from(localizationMessageService.create(request));
        return ResponseEntity.status(201).body(payload);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalizationMessageResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody LocalizationMessageRequest request
    ) {
        LocalizationMessageResponse payload = LocalizationMessageResponse.from(localizationMessageService.update(id, request));
        return ResponseEntity.ok(payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        localizationMessageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<LocalizationMessageResponse>> findAllByLanguage(
            @PathVariable @Pattern(regexp = "^[a-zA-Z]{2}$", message = "language must be a two-letter ISO 639-1 code") String language
    ) {
        List<LocalizationMessageResponse> payload = localizationMessageService.findAllByLanguage(language).stream()
                .map(LocalizationMessageResponse::from)
                .toList();
        return ResponseEntity.ok(payload);
    }
}
