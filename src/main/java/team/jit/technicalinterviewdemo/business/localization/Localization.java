package team.jit.technicalinterviewdemo.business.localization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Locale;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "localization_messages", uniqueConstraints = @UniqueConstraint(
                name = "uk_localization_messages_message_key_language", columnNames = {"message_key", "language"}
        )
)
public class Localization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "message_key", nullable = false, length = 150)
    private String messageKey;

    @Column(nullable = false, length = 8)
    private String language;

    @Column(name = "message_text", nullable = false, length = 2000)
    private String messageText;

    @Column(length = 1000)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    public Localization(String messageKey, String language, String messageText, String description) {
        setMessageKey(messageKey);
        setLanguage(language);
        setMessageText(messageText);
        setDescription(description);
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = normalizeRequired(messageKey, "messageKey");
    }

    public void setLanguage(String language) {
        this.language = normalizeRequired(language, "language").toLowerCase(Locale.ROOT);
    }

    public void setMessageText(String messageText) {
        this.messageText = normalizeRequired(messageText, "messageText");
    }

    public void setDescription(String description) {
        this.description = normalizeOptional(description);
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
