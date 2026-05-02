package team.jit.technicalinterviewdemo.business.category;

public class CategoryInUseException extends RuntimeException {

    public CategoryInUseException(Long id, String name) {
        super("Category '%s' with id %d cannot be deleted because it is still assigned to one or more books.".formatted(name, id));
    }
}
