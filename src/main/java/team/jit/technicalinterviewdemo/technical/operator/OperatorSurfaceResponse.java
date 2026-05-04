package team.jit.technicalinterviewdemo.technical.operator;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import team.jit.technicalinterviewdemo.business.audit.AuditLogResponse;
import team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewResponse;

@Schema(name = "OperatorSurfaceResponse", description = "ADMIN-only operational inspection surface.")
public record OperatorSurfaceResponse(
        @Schema(description = "Audit visibility section.")
        AuditSection audit,
        @Schema(description = "Runtime diagnostics section.")
        RuntimeDiagnostics runtime,
        @Schema(description = "Operational status section.")
        OperationalStatus operations
) {

    @Schema(name = "OperatorAuditSection", description = "Audit endpoint summary for operators.")
    public record AuditSection(
            @Schema(description = "Endpoint path for full audit log pagination.", example = "/api/admin/audit-logs")
            String auditLogEndpoint,
            @Schema(description = "Total number of audit log entries currently stored.", example = "42")
            long totalEntries,
            @ArraySchema(arraySchema = @Schema(description = "Most recent audit entries, newest first."))
            List<AuditLogResponse> recentEntries
    ) {
    }

    @Schema(name = "OperatorRuntimeDiagnostics", description = "Runtime diagnostics available to operators.")
    public record RuntimeDiagnostics(
            @Schema(description = "Endpoint path for the full technical overview.", example = "/")
            String technicalOverviewEndpoint,
            @Schema(description = "Current technical overview payload.")
            TechnicalOverviewResponse technicalOverview
    ) {
    }

    @Schema(name = "OperatorOperationalStatus", description = "Operational status links and current readiness state.")
    public record OperationalStatus(
            @Schema(description = "Endpoint path for actuator health.", example = "/actuator/health")
            String actuatorHealthEndpoint,
            @Schema(description = "Endpoint path for actuator info.", example = "/actuator/info")
            String actuatorInfoEndpoint,
            @Schema(description = "Endpoint path for deployment-scoped Prometheus scraping.", example = "/actuator/prometheus")
            String actuatorPrometheusEndpoint,
            @Schema(description = "Current aggregated health status code.", example = "UP")
            String applicationHealthStatus,
            @Schema(description = "Current Spring Boot liveness state.", example = "CORRECT")
            String livenessState,
            @Schema(description = "Current Spring Boot readiness state.", example = "ACCEPTING_TRAFFIC")
            String readinessState
    ) {
    }
}
