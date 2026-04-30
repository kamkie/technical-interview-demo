package team.jit.technicalinterviewdemo.business.category;

import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        List<Category> payload = categoryService.findAll();
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryCreateRequest request) {
        Category payload = categoryService.create(request);
        return ResponseEntity.status(201).body(payload);
    }
}
