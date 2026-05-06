package team.jit.technicalinterviewdemo.technical.operator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditLogResponse;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserRole;
import team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewResponse;
import team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OperatorSurfaceService {

    static final String OPERATOR_AUDIT_ENDPOINT = "/api/admin/audit-logs";
    static final String OPERATOR_TECHNICAL_OVERVIEW_ENDPOINT = "/";
    static final String OPERATOR_ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";
    static final String OPERATOR_ACTUATOR_INFO_ENDPOINT = "/actuator/info";
    static final String OPERATOR_ACTUATOR_PROMETHEUS_ENDPOINT = "/actuator/prometheus";
    private static final int RECENT_AUDIT_LIMIT = 10;

    private final CurrentUserAccountService currentUserAccountService;
    private final AuditLogRepository auditLogRepository;
    private final TechnicalOverviewService technicalOverviewService;
    private final HealthEndpoint healthEndpoint;
    private final ApplicationAvailability applicationAvailability;

    public OperatorSurfaceResponse getOperatorSurface() {
        requireAdminRole();
        return new OperatorSurfaceResponse(
            buildAuditSection(), buildRuntimeDiagnostics(), buildOperationalStatus()
        );
    }

    private void requireAdminRole() {
        currentUserAccountService.requireRole(UserRole.ADMIN, "Operator surface requires the ADMIN role.");
    }

    private OperatorSurfaceResponse.AuditSection buildAuditSection() {
        return new OperatorSurfaceResponse.AuditSection(
            OPERATOR_AUDIT_ENDPOINT, auditLogRepository.count(), loadRecentAuditEntries()
        );
    }

    private List<AuditLogResponse> loadRecentAuditEntries() {
        return auditLogRepository.findAll(PageRequest.of(0, RECENT_AUDIT_LIMIT, Sort.by(Sort.Direction.DESC, "id"))).map(AuditLogResponse::from).getContent();
    }

    private OperatorSurfaceResponse.RuntimeDiagnostics buildRuntimeDiagnostics() {
        TechnicalOverviewResponse technicalOverview = technicalOverviewService.getOverview();
        return new OperatorSurfaceResponse.RuntimeDiagnostics(
            OPERATOR_TECHNICAL_OVERVIEW_ENDPOINT, technicalOverview
        );
    }

    private OperatorSurfaceResponse.OperationalStatus buildOperationalStatus() {
        String healthStatus = healthEndpoint.health().getStatus().getCode();
        return new OperatorSurfaceResponse.OperationalStatus(
            OPERATOR_ACTUATOR_HEALTH_ENDPOINT, OPERATOR_ACTUATOR_INFO_ENDPOINT, OPERATOR_ACTUATOR_PROMETHEUS_ENDPOINT, healthStatus, applicationAvailability.getLivenessState().name(), applicationAvailability.getReadinessState().name()
        );
    }
}
