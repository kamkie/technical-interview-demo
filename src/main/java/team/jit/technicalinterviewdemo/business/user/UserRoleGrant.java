package team.jit.technicalinterviewdemo.business.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(UserRoleGrantId.class)
@Table(name = "user_roles")
public class UserRoleGrant {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "grant_source", nullable = false, length = 50)
    private UserRoleGrantSource grantSource;

    @Column(name = "granted_at", nullable = false)
    private Instant grantedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by_user_id")
    private UserAccount grantedByUser;

    @Column(name = "reason", length = 255)
    private String reason;

    public UserRoleGrant(
            UserAccount userAccount,
            UserRole role,
            UserRoleGrantSource grantSource,
            UserAccount grantedByUser,
            String reason
    ) {
        this.userAccount = Objects.requireNonNull(userAccount, "userAccount is required");
        this.role = Objects.requireNonNull(role, "role is required");
        this.grantSource = Objects.requireNonNull(grantSource, "grantSource is required");
        this.grantedAt = Instant.now();
        this.grantedByUser = grantedByUser;
        this.reason = normalizeOptional(reason);

        if (grantSource == UserRoleGrantSource.ADMIN_MANAGED) {
            Objects.requireNonNull(grantedByUser, "grantedByUser is required");
            if (this.reason == null) {
                throw new IllegalArgumentException("reason is required");
            }
        }
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
