package team.jit.technicalinterviewdemo.business.user;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Schema(name = "UserAccountResponse", description = "Persisted application profile for the current authenticated user.")
public record UserAccountResponse(
        @Schema(description = "Database identifier.", example = "1")
        Long id,
        @Schema(description = "OAuth provider name.", example = "github")
        String provider,
        @Schema(description = "External login from the authenticated OAuth provider.", example = "kamkie")
        String login,
        @Schema(description = "Display name from the OAuth provider.", example = "Kamil Kiewisz")
        String displayName,
        @Schema(description = "Email address when available from the provider.")
        String email,
        @Schema(description = "Preferred two-letter language code used as the last localization fallback.")
        String preferredLanguage,
        @ArraySchema(
                schema = @Schema(description = "Granted role name.", example = "USER"),
                arraySchema = @Schema(description = "Roles assigned to the persisted user.")
        )
        List<String> roles,
        @Schema(description = "Timestamp of the latest authenticated request.")
        LocalDateTime lastLoginAt,
        @Schema(description = "Creation timestamp.")
        LocalDateTime createdAt,
        @Schema(description = "Last update timestamp.")
        LocalDateTime updatedAt
) {

    public static UserAccountResponse from(UserAccount userAccount) {
        List<String> roles = userAccount.getRoles().stream()
                .map(Enum::name)
                .sorted(Comparator.naturalOrder())
                .toList();
        return new UserAccountResponse(
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
