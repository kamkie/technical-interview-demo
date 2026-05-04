package team.jit.technicalinterviewdemo.business.audit;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Schema(name = "AuditLogResponse", description = "Recorded audit log entry.")
public record AuditLogResponse(
        @Schema(description = "Database identifier.", example = "42")
        Long id,
        @Schema(description = "Type of audited target.", example = "BOOK")
        AuditTargetType targetType,
        @Schema(description = "Identifier of the audited target.", example = "7")
        Long targetId,
        @Schema(description = "Recorded action.", example = "UPDATE")
        AuditAction action,
        @Schema(description = "Login of the acting user when known, or `system` for non-user writes.", example = "admin-user")
        String actorLogin,
        @Schema(description = "Human-readable audit summary.", example = "Updated book 'Clean Code' with ISBN 9780132350884.")
        String summary,
        @Schema(description = "Structured audit details kept compact and safe for ADMIN review.")
        Map<String, Object> details,
        @Schema(description = "Creation timestamp in UTC.")
        LocalDateTime createdAt
) {

    public static AuditLogResponse from(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getTargetType(),
                auditLog.getTargetId(),
                auditLog.getAction(),
                auditLog.getActorLogin(),
                auditLog.getSummary(),
                auditLog.getDetails(),
                toUtcLocalDateTime(auditLog.getCreatedAt())
        );
    }

    private static LocalDateTime toUtcLocalDateTime(Instant timestamp) {
        return timestamp == null ? null : LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC);
    }
}
