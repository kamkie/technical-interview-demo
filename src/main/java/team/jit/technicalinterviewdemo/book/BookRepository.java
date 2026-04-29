package team.jit.technicalinterviewdemo.book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);
}
