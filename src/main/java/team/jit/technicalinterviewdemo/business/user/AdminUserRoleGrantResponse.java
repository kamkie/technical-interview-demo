package team.jit.technicalinterviewdemo.business.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(name = "AdminUserRoleGrantResponse", description = "Persisted provenance for one granted application role.")
public record AdminUserRoleGrantResponse(
        @Schema(description = "Granted role name.", example = "ADMIN")
        String role,
        @Schema(description = "How the role grant entered application state.", example = "BOOTSTRAP")
        UserRoleGrantSource source,
        @Schema(description = "UTC timestamp when this role grant was recorded.")
        LocalDateTime grantedAt,
        @Schema(description = "Granting application user id when the role was assigned manually.", example = "1")
        Long grantedByUserId,
        @Schema(description = "Granting application user login when the role was assigned manually.", example = "admin-user")
        String grantedByLogin,
        @Schema(description = "Optional operator-supplied reason captured for manually managed grants.")
        String reason
) {

    public static AdminUserRoleGrantResponse from(UserRoleGrant roleGrant) {
        UserAccount grantedByUser = roleGrant.getGrantedByUser();
        return new AdminUserRoleGrantResponse(
                roleGrant.getRole().name(),
                roleGrant.getGrantSource(),
                roleGrant.getGrantedAt(),
                grantedByUser == null ? null : grantedByUser.getId(),
                grantedByUser == null ? null : grantedByUser.getExternalLogin(),
                roleGrant.getReason()
        );
    }
}
