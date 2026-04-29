package team.jit.technicalinterviewdemo.book;

import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> findAll() {
        List<Book> payload = bookService.findAll();
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> findById(@PathVariable Long id) {
        Book payload = bookService.findById(id);
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody BookCreateRequest request) {
        Book payload = bookService.create(request);
        return ResponseEntity.status(201).body(payload);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @Valid @RequestBody BookUpdateRequest request) {
        Book payload = bookService.update(id, request);
        return ResponseEntity.ok(payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
