package team.jit.technicalinterviewdemo.user;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record UserProfileResponse(
        Long id,
        String provider,
        String login,
        String displayName,
        String email,
        String preferredLanguage,
        List<String> roles,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UserProfileResponse from(UserAccount userAccount) {
        List<String> roles = userAccount.getRoles().stream()
                .map(Enum::name)
                .sorted(Comparator.naturalOrder())
                .toList();
        return new UserProfileResponse(
                userAccount.getId(),
                userAccount.getProvider(),
                userAccount.getExternalLogin(),
                userAccount.getDisplayName(),
                userAccount.getEmail(),
                userAccount.getPreferredLanguage(),
                roles,
                userAccount.getLastLoginAt(),
                userAccount.getCreatedAt(),
                userAccount.getUpdatedAt()
        );
    }
}
