package team.jit.technicalinterviewdemo.business.category;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Public category reads with ADMIN-only category management.")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "List categories", description = "Public endpoint that returns categories ordered by name.")
    public ResponseEntity<List<Category>> findAll() {
        List<Category> payload = categoryService.findAll();
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    @Operation(
            summary = "Create a category",
            description = "Requires an authenticated session with the ADMIN role.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryCreateRequest request) {
        Category payload = categoryService.create(request);
        return ResponseEntity.status(201).body(payload);
    }
}
