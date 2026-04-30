package team.jit.technicalinterviewdemo.user;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_users_provider_external_login",
                columnNames = {"provider", "external_login"}
        )
)
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(name = "external_login", nullable = false, length = 100)
    private String externalLogin;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(length = 255)
    private String email;

    @Column(name = "preferred_language", length = 8)
    private String preferredLanguage;

    @Column(name = "last_login_at", nullable = false)
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private Set<UserRole> roles = new LinkedHashSet<>();

    public UserAccount(
            String provider,
            String externalLogin,
            String displayName,
            String email,
            String preferredLanguage,
            LocalDateTime lastLoginAt,
            Set<UserRole> roles
    ) {
        setProvider(provider);
        setExternalLogin(externalLogin);
        setDisplayName(displayName);
        setEmail(email);
        setPreferredLanguage(preferredLanguage);
        setLastLoginAt(lastLoginAt);
        setRoles(roles);
    }

    public void setProvider(String provider) {
        this.provider = normalizeRequired(provider, "provider").toLowerCase(Locale.ROOT);
    }

    public void setExternalLogin(String externalLogin) {
        this.externalLogin = normalizeRequired(externalLogin, "externalLogin").toLowerCase(Locale.ROOT);
    }

    public void setDisplayName(String displayName) {
        this.displayName = normalizeOptional(displayName);
    }

    public void setEmail(String email) {
        String normalizedEmail = normalizeOptional(email);
        this.email = normalizedEmail == null ? null : normalizedEmail.toLowerCase(Locale.ROOT);
    }

    public void setPreferredLanguage(String preferredLanguage) {
        String normalizedPreferredLanguage = normalizeOptional(preferredLanguage);
        this.preferredLanguage = normalizedPreferredLanguage == null
                ? null
                : normalizedPreferredLanguage.toLowerCase(Locale.ROOT);
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        if (lastLoginAt == null) {
            throw new IllegalArgumentException("lastLoginAt is required");
        }
        this.lastLoginAt = lastLoginAt;
    }

    public void setRoles(Set<UserRole> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("roles are required");
        }
        this.roles = new LinkedHashSet<>(roles);
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    private String normalizeRequired(String value, String fieldName) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return normalized;
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
