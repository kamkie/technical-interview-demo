package team.jit.technicalinterviewdemo.business.user;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Schema(description = "Payload for replacing the managed role set of one persisted user.")
public record AdminUserRoleUpdateRequest(
        @ArraySchema(
                schema = @Schema(description = "Role name to keep assigned after replacement.", example = "ADMIN"),
                arraySchema = @Schema(description = "Replacement role set. `USER` must always be present.")
        )
        @NotEmpty(message = "roles are required")
        Set<@NotNull(message = "roles must not contain null values") UserRole> roles,
        @Schema(description = "Short operator-supplied explanation for the role change.", example = "Needs audit review access.")
        @NotBlank(message = "reason is required")
        @Size(max = 255, message = "reason must be at most 255 characters")
        String reason
) {

    public Set<UserRole> requestedRoles() {
        return roles == null ? Set.of() : new LinkedHashSet<>(roles);
    }
}
