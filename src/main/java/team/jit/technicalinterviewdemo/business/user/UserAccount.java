package team.jit.technicalinterviewdemo.business.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "uk_users_provider_external_login",
                        columnNames = {"provider", "external_login"}))
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
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("role ASC")
    private List<UserRoleGrant> roleGrants = new ArrayList<>();

    public UserAccount(
            String provider,
            String externalLogin,
            String displayName,
            String email,
            String preferredLanguage,
            Instant lastLoginAt,
            Set<UserRole> roles) {
        setProvider(provider);
        setExternalLogin(externalLogin);
        setDisplayName(displayName);
        setEmail(email);
        setPreferredLanguage(preferredLanguage);
        setLastLoginAt(lastLoginAt);
        initializeRoleGrants(roles);
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
        this.preferredLanguage =
                normalizedPreferredLanguage == null ? null : normalizedPreferredLanguage.toLowerCase(Locale.ROOT);
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        if (lastLoginAt == null) {
            throw new IllegalArgumentException("lastLoginAt is required");
        }
        this.lastLoginAt = lastLoginAt;
    }

    public Set<UserRole> getRoles() {
        return roleGrants.stream()
                .map(UserRoleGrant::getRole)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    public List<UserRoleGrant> getRoleGrants() {
        return roleGrants.stream()
                .sorted(Comparator.comparing(grant -> grant.getRole().name()))
                .toList();
    }

    public boolean hasRole(UserRole role) {
        return findRoleGrant(role).isPresent();
    }

    public void ensureRoleGrant(
            UserRole role, UserRoleGrantSource grantSource, UserAccount grantedByUser, String reason) {
        if (findRoleGrant(role).isPresent()) {
            return;
        }
        roleGrants.add(new UserRoleGrant(this, role, grantSource, grantedByUser, reason));
    }

    public void replaceManagedRoleGrants(Set<UserRole> roles, UserAccount grantedByUser, String reason) {
        Set<UserRole> normalizedRoles = normalizeManagedRoles(roles);
        UserAccount normalizedGrantor = Objects.requireNonNull(grantedByUser, "grantedByUser is required");
        String normalizedReason = normalizeRequired(reason, "reason");

        roleGrants.removeIf(grant -> grant.getGrantSource() != UserRoleGrantSource.BOOTSTRAP);
        for (UserRole role : normalizedRoles) {
            roleGrants.add(new UserRoleGrant(
                    this, role, UserRoleGrantSource.ADMIN_MANAGED, normalizedGrantor, normalizedReason));
        }
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    private void initializeRoleGrants(Set<UserRole> roles) {
        Set<UserRole> normalizedRoles = normalizeRequiredRoles(roles);
        roleGrants.clear();
        for (UserRole role : normalizedRoles) {
            roleGrants.add(new UserRoleGrant(this, role, UserRoleGrantSource.AUTHENTICATED_LOGIN, null, null));
        }
    }

    private Set<UserRole> normalizeManagedRoles(Set<UserRole> roles) {
        Set<UserRole> normalizedRoles = normalizeRequiredRoles(roles);
        if (!normalizedRoles.contains(UserRole.USER)) {
            throw new IllegalArgumentException("USER role is required");
        }
        return normalizedRoles;
    }

    private Set<UserRole> normalizeRequiredRoles(Set<UserRole> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("roles are required");
        }

        Set<UserRole> normalizedRoles = new LinkedHashSet<>();
        for (UserRole role : roles) {
            if (role == null) {
                throw new IllegalArgumentException("roles must not contain null values");
            }
            normalizedRoles.add(role);
        }
        return normalizedRoles;
    }

    private Optional<UserRoleGrant> findRoleGrant(UserRole role) {
        return roleGrants.stream().filter(grant -> grant.getRole() == role).findFirst();
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
