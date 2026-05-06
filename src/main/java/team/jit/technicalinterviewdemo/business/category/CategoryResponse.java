package team.jit.technicalinterviewdemo.business.category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Category", description = "Book category.")
public record CategoryResponse(
    @Schema(description = "Database identifier.", example = "1") Long id,
    @Schema(description = "Unique category name.", example = "Java") String name
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
