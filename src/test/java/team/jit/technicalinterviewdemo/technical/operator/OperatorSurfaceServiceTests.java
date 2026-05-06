package team.jit.technicalinterviewdemo.technical.operator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.health.actuate.endpoint.HealthDescriptor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.health.actuate.endpoint.IndicatedHealthDescriptor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLog;
import team.jit.technicalinterviewdemo.business.audit.AuditLogRepository;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserRole;
import team.jit.technicalinterviewdemo.technical.api.ForbiddenOperationException;
import team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewResponse;
import team.jit.technicalinterviewdemo.technical.info.TechnicalOverviewService;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorSurfaceServiceTests {

    private static final String ADMIN_ROLE_MESSAGE = "Operator surface requires the ADMIN role.";
    private static final PageRequest RECENT_AUDIT_PAGE_REQUEST =
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

    @Mock
    private CurrentUserAccountService currentUserAccountService;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private TechnicalOverviewService technicalOverviewService;

    @Mock
    private HealthEndpoint healthEndpoint;

    @Mock
    private ApplicationAvailability applicationAvailability;

    private OperatorSurfaceService operatorSurfaceService;

    @BeforeEach
    void setUp() {
        operatorSurfaceService = new OperatorSurfaceService(
                currentUserAccountService,
                auditLogRepository,
                technicalOverviewService,
                healthEndpoint,
                applicationAvailability
        );
    }

    @Test
    void getOperatorSurfaceRequiresAdminRoleBeforeLoadingDiagnostics() {
        doThrow(new ForbiddenOperationException(ADMIN_ROLE_MESSAGE))
                .when(currentUserAccountService)
                .requireRole(UserRole.ADMIN, ADMIN_ROLE_MESSAGE);

        assertThatThrownBy(() -> operatorSurfaceService.getOperatorSurface())
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessage(ADMIN_ROLE_MESSAGE);

        verify(currentUserAccountService).requireRole(UserRole.ADMIN, ADMIN_ROLE_MESSAGE);
        verifyNoInteractions(auditLogRepository, technicalOverviewService, healthEndpoint, applicationAvailability);
    }

    @Test
    void getOperatorSurfaceAssemblesAuditRuntimeAndOperationalSections() {
        AuditLog newestEntry = auditLogWithId(15L, "error.book.not_found", "fr");
        AuditLog olderEntry = auditLogWithId(14L, "book.updated", "en");
        TechnicalOverviewResponse technicalOverview = technicalOverview();
        when(auditLogRepository.findAll(RECENT_AUDIT_PAGE_REQUEST))
                .thenReturn(new PageImpl<>(List.of(newestEntry, olderEntry), RECENT_AUDIT_PAGE_REQUEST, 2));
        when(auditLogRepository.count()).thenReturn(42L);
        when(technicalOverviewService.getOverview()).thenReturn(technicalOverview);
        when(healthEndpoint.health()).thenReturn(healthDescriptor("UP"));
        when(applicationAvailability.getLivenessState()).thenReturn(LivenessState.CORRECT);
        when(applicationAvailability.getReadinessState()).thenReturn(ReadinessState.ACCEPTING_TRAFFIC);

        OperatorSurfaceResponse response = operatorSurfaceService.getOperatorSurface();

        verify(currentUserAccountService).requireRole(UserRole.ADMIN, ADMIN_ROLE_MESSAGE);
        assertThat(response.audit().auditLogEndpoint()).isEqualTo("/api/admin/audit-logs");
        assertThat(response.audit().totalEntries()).isEqualTo(42L);
        assertThat(response.audit().recentEntries())
                .extracting(entry -> entry.id())
                .containsExactly(15L, 14L);
        assertThat(response.audit().recentEntries().getFirst().details())
                .containsEntry("messageKey", "error.book.not_found")
                .containsEntry("language", "fr");
        assertThat(response.runtime().technicalOverviewEndpoint()).isEqualTo("/");
        assertThat(response.runtime().technicalOverview()).isEqualTo(technicalOverview);
        assertThat(response.operations().actuatorHealthEndpoint()).isEqualTo("/actuator/health");
        assertThat(response.operations().actuatorInfoEndpoint()).isEqualTo("/actuator/info");
        assertThat(response.operations().actuatorPrometheusEndpoint()).isEqualTo("/actuator/prometheus");
        assertThat(response.operations().applicationHealthStatus()).isEqualTo("UP");
        assertThat(response.operations().livenessState()).isEqualTo("CORRECT");
        assertThat(response.operations().readinessState()).isEqualTo("ACCEPTING_TRAFFIC");
    }

    @Test
    void getOperatorSurfaceRequestsNewestTenAuditEntries() {
        when(auditLogRepository.findAll(RECENT_AUDIT_PAGE_REQUEST))
                .thenReturn(new PageImpl<>(List.of(), RECENT_AUDIT_PAGE_REQUEST, 0));
        when(auditLogRepository.count()).thenReturn(0L);
        when(technicalOverviewService.getOverview()).thenReturn(technicalOverview());
        when(healthEndpoint.health()).thenReturn(healthDescriptor("UP"));
        when(applicationAvailability.getLivenessState()).thenReturn(LivenessState.CORRECT);
        when(applicationAvailability.getReadinessState()).thenReturn(ReadinessState.ACCEPTING_TRAFFIC);

        OperatorSurfaceResponse response = operatorSurfaceService.getOperatorSurface();

        verify(auditLogRepository).findAll(RECENT_AUDIT_PAGE_REQUEST);
        assertThat(response.audit().recentEntries()).isEmpty();
    }

    private AuditLog auditLogWithId(long id, String messageKey, String language) {
        AuditLog auditLog = new AuditLog(
                AuditTargetType.LOCALIZATION_MESSAGE,
                id,
                AuditAction.DELETE,
                null,
                "admin-user",
                "Deleted localization message '%s' in language %s.".formatted(messageKey, language),
                Map.of(
                        "messageKey", messageKey,
                        "language", language
                )
        );
        ReflectionTestUtils.setField(auditLog, "id", id);
        ReflectionTestUtils.setField(auditLog, "createdAt", Instant.parse("2026-05-04T08:30:00Z"));
        return auditLog;
    }

    private TechnicalOverviewResponse technicalOverview() {
        return new TechnicalOverviewResponse(
                new TechnicalOverviewResponse.BuildDetails(
                        "technical-interview-demo",
                        "team.jit",
                        "technical-interview-demo",
                        "2.0.0-M4",
                        Instant.parse("2026-05-04T08:00:00Z")
                ),
                new TechnicalOverviewResponse.GitDetails(
                        "main",
                        "f".repeat(40),
                        "fffffff",
                        Instant.parse("2026-05-04T08:00:00Z")
                ),
                new TechnicalOverviewResponse.RuntimeDetails(
                        "technical-interview-demo",
                        "25",
                        "Azul",
                        List.of("test")
                ),
                Map.of("spring-boot", "3.5.0"),
                new TechnicalOverviewResponse.ConfigurationDetails(
                        new TechnicalOverviewResponse.PaginationDetails(20, 100),
                        new TechnicalOverviewResponse.SessionDetails(
                                "jdbc",
                                "15m",
                                "SESSION",
                                true,
                                "Lax"
                        ),
                        new TechnicalOverviewResponse.ObservabilityDetails(
                                List.of("health", "info", "prometheus"),
                                true,
                                1.0
                        ),
                        new TechnicalOverviewResponse.DocumentationDetails(
                                "/docs",
                                "/v3/api-docs",
                                "/v3/api-docs.yaml",
                                "3.1.0"
                        ),
                        new TechnicalOverviewResponse.SecurityDetails(
                                true,
                                "XSRF-TOKEN",
                                "X-XSRF-TOKEN",
                                true,
                                "/api/**",
                                "/oauth2/authorization",
                                "/login/oauth2/code/{registrationId}",
                                "framework",
                                new TechnicalOverviewResponse.AbuseProtectionDetails(
                                        "edge-or-gateway",
                                        "/oauth2/authorization/{registrationId}",
                                        List.of("csrf", "rate-limit"),
                                        "/api/**",
                                        List.of("PUT /api/account/language"),
                                        List.of("csrf", "authentication")
                                )
                        ),
                        new TechnicalOverviewResponse.ShutdownDetails(
                                "graceful",
                                "30s"
                        )
                )
        );
    }

    private HealthDescriptor healthDescriptor(String status) {
        try {
            var constructor = IndicatedHealthDescriptor.class.getDeclaredConstructor(Health.class);
            constructor.setAccessible(true);
            return constructor.newInstance(Health.status(status).build());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException exception) {
            throw new LinkageError("Failed to create HealthDescriptor test fixture.", exception);
        }
    }
}
