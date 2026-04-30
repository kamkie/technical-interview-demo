package team.jit.technicalinterviewdemo.book;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchRequest {

    private String title;
    private String author;
    private String isbn;
    private Integer year;
    private Integer yearFrom;
    private Integer yearTo;
}
