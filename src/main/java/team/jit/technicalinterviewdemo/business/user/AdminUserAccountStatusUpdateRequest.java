package team.jit.technicalinterviewdemo.business.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for replacing the account status of one persisted user.")
public record AdminUserAccountStatusUpdateRequest(
        @Schema(description = "Requested account status.", example = "BLOCKED") @NotNull(message = "status is required")
        UserAccountStatus status,

        @Schema(
                description = "Short operator-supplied explanation for the status change. Persisted as the block"
                        + " reason when blocking; recorded only in the audit entry when unblocking.",
                example = "Abusive API usage pending review.")
        @NotBlank(message = "reason is required")
        @Size(max = 255, message = "reason must be at most 255 characters")
        String reason) {}
