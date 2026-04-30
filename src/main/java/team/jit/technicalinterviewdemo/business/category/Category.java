package team.jit.technicalinterviewdemo.business.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "categories")
@Schema(description = "Book category.")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Schema(description = "Database identifier.", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Unique category name.", example = "Java")
    private String name;

    public Category(String name) {
        this.name = name;
    }
}
