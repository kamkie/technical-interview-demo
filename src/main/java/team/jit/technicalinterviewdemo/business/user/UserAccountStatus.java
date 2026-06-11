package team.jit.technicalinterviewdemo.business.user;

public enum UserAccountStatus {
    ACTIVE,
    BLOCKED;

    public static UserAccountStatus of(UserAccount userAccount) {
        return userAccount.isBlocked() ? BLOCKED : ACTIVE;
    }
}
