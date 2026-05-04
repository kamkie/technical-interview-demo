package team.jit.technicalinterviewdemo.business.user;

public class UserAccountNotFoundException extends RuntimeException {

    public UserAccountNotFoundException(Long id) {
        super("User account with id %d was not found.".formatted(id));
    }
}
