package team.jit.technicalinterviewdemo.business.book;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Query filters for searching books.")
public class BookSearchRequest {

    @Schema(description = "Case-insensitive substring filter for the title.", example = "clean")
    private String title;

    @Schema(description = "Case-insensitive substring filter for the author.", example = "bloch")
    private String author;

    @Schema(description = "Case-insensitive substring filter for the ISBN.", example = "978013")
    private String isbn;

    @Schema(description = "Exact publication year. Cannot be combined with yearFrom or yearTo.", example = "2018")
    private Integer year;

    @Schema(description = "Inclusive publication-year lower bound.", example = "2000")
    private Integer yearFrom;

    @Schema(description = "Inclusive publication-year upper bound.", example = "2020")
    private Integer yearTo;

    @ArraySchema(
            schema = @Schema(description = "Category filter value.", example = "Java"),
            arraySchema = @Schema(description = "Repeat the parameter to match any of multiple categories."))
    private List<String> category;
}
