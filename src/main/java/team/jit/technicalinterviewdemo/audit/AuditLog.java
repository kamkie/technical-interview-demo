package team.jit.technicalinterviewdemo.audit;

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

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.jit.technicalinterviewdemo.user.UserAccount;

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

    @Column(name = "target_id", nullable = false)
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

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    public AuditLog(
            AuditTargetType targetType,
            Long targetId,
            AuditAction action,
            UserAccount actorUser,
            String actorLogin,
            String summary
    ) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.action = action;
        this.actorUser = actorUser;
        this.actorLogin = actorLogin;
        this.summary = summary;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now(ZoneOffset.UTC);
    }
}
