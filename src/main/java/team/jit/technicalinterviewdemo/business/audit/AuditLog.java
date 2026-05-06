package team.jit.technicalinterviewdemo.business.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import team.jit.technicalinterviewdemo.business.user.UserAccount;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    private AuditTargetType targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_user_id")
    private UserAccount actorUser;

    @Column(name = "actor_login", length = 100)
    private String actorLogin;

    @Column(nullable = false, length = 1000)
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> details = new LinkedHashMap<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    public AuditLog(
                    AuditTargetType targetType, Long targetId, AuditAction action, UserAccount actorUser, String actorLogin, String summary
    ) {
        this(targetType, targetId, action, actorUser, actorLogin, summary, Map.of());
    }

    public AuditLog(
                    AuditTargetType targetType, Long targetId, AuditAction action, UserAccount actorUser, String actorLogin, String summary, Map<String, Object> details
    ) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.action = action;
        this.actorUser = actorUser;
        this.actorLogin = actorLogin;
        this.summary = summary;
        this.details = normalizeDetails(details);
    }

    public Map<String, Object> getDetails() {
        return Map.copyOf(details);
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    private Map<String, Object> normalizeDetails(Map<String, Object> details) {
        if (details == null || details.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return new LinkedHashMap<>(details);
    }
}
