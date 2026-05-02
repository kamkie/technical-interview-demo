package team.jit.technicalinterviewdemo.business.category;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Category with id %d was not found.".formatted(id));
    }
}
