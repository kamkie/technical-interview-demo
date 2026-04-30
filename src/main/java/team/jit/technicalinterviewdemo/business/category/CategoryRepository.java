package team.jit.technicalinterviewdemo.business.category;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<Category> findAllByOrderByNameAsc();

    @Query("select category from Category category where lower(category.name) in :names order by category.name asc")
    List<Category> findAllByNormalizedNames(@Param("names") Collection<String> names);
}
