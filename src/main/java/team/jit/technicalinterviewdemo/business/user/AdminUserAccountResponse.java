package team.jit.technicalinterviewdemo.business.user;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Schema(
        name = "AdminUserAccountResponse",
        description = "Persisted application user profile plus role-grant provenance for ADMIN review.")
public record AdminUserAccountResponse(
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
                arraySchema = @Schema(description = "Current roles assigned to the persisted user."))
        List<String> roles,

        @ArraySchema(arraySchema = @Schema(description = "Persisted role-grant provenance entries."))
        List<AdminUserRoleGrantResponse> roleGrants,

        @Schema(description = "UTC instant of the latest authenticated request.")
        Instant lastLoginAt,

        @Schema(description = "Creation timestamp as a UTC instant.")
        Instant createdAt,

        @Schema(description = "Last update timestamp as a UTC instant.")
        Instant updatedAt) {

    public static AdminUserAccountResponse from(UserAccount userAccount) {
        List<String> roles = userAccount.getRoles().stream()
                .map(Enum::name)
                .sorted(Comparator.naturalOrder())
                .toList();
        List<AdminUserRoleGrantResponse> roleGrants = userAccount.getRoleGrants().stream()
                .map(AdminUserRoleGrantResponse::from)
                .toList();
        return new AdminUserAccountResponse(
                userAccount.getId(),
                userAccount.getProvider(),
                userAccount.getExternalLogin(),
                userAccount.getDisplayName(),
                userAccount.getEmail(),
                userAccount.getPreferredLanguage(),
                roles,
                roleGrants,
                userAccount.getLastLoginAt(),
                userAccount.getCreatedAt(),
                userAccount.getUpdatedAt());
    }
}
