package team.jit.technicalinterviewdemo.business.book;

public class StaleBookVersionException extends RuntimeException {

    public StaleBookVersionException(Long id, Long requestedVersion, Long currentVersion) {
        this(id, requestedVersion, currentVersion, null);
    }

    public StaleBookVersionException(Long id, Long requestedVersion, Long currentVersion, Throwable cause) {
        super(
                "Book with id %d is at version %d. Retry the update with the latest version instead of %d.".formatted(id, currentVersion, requestedVersion), cause
        );
    }
}
