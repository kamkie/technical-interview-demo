package team.jit.technicalinterviewdemo.business.localization;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;
import team.jit.technicalinterviewdemo.business.user.UserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserRole;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocalizationMessageService {

    private static final Pattern MESSAGE_KEY_PATTERN = Pattern.compile("^[a-z0-9._-]+$");
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("^[a-zA-Z]{2}$");
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "messageKey", "language", "createdAt", "updatedAt");

    private final LocalizationMessageRepository localizationMessageRepository;
    private final LocalizationContext localizationContext;
    private final CacheManager cacheManager;
    private final ApplicationMetrics applicationMetrics;
    private final UserAccountService userAccountService;
    private final AuditLogService auditLogService;

    public Page<LocalizationMessage> findAll(Pageable pageable) {
        applicationMetrics.recordLocalizationOperation("list");
        Pageable effectivePageable = createEffectivePageable(pageable);
        return localizationMessageRepository.findAll(effectivePageable);
    }

    public LocalizationMessage findById(Long id) {
        applicationMetrics.recordLocalizationOperation("get");
        return localizationMessageRepository.findById(id)
                .orElseThrow(() -> new LocalizationMessageNotFoundException(id));
    }

    public LocalizationMessage findByMessageKeyAndLanguage(String messageKey, String language) {
        applicationMetrics.recordLocalizationOperation("lookupExact");
        return findMessage(normalizeMessageKey(messageKey), normalizeSupportedLanguage(language))
                .orElseThrow(() -> new LocalizationMessageNotFoundException(messageKey, language));
    }

    public String getMessage(String messageKey, String language) {
        return findByMessageKeyAndLanguage(messageKey, language).getMessageText();
    }

    public LocalizationMessage findByMessageKeyAndLanguageWithFallback(String messageKey, String language, String fallbackLanguage) {
        String normalizedMessageKey = normalizeMessageKey(messageKey);
        String normalizedLanguage = normalizeLanguage(language);
        String normalizedFallbackLanguage = normalizeSupportedLanguage(fallbackLanguage);
        String lookupCacheKey = "%s::%s::%s".formatted(normalizedMessageKey, normalizedLanguage, normalizedFallbackLanguage);
        applicationMetrics.recordLocalizationOperation("lookupWithFallback");

        Cache localizationLookupCache = requireCache(CacheNames.LOCALIZATION_LOOKUPS);
        LocalizationMessage cachedMessage = localizationLookupCache.get(lookupCacheKey, LocalizationMessage.class);
        if (cachedMessage != null) {
            applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_LOOKUPS, "hit");
            return cachedMessage;
        }

        applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_LOOKUPS, "miss");

        Optional<LocalizationMessage> requestedMessage = findMessage(normalizedMessageKey, normalizedLanguage);
        if (requestedMessage.isPresent()) {
            localizationLookupCache.put(lookupCacheKey, requestedMessage.get());
            applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_LOOKUPS, "put");
            return requestedMessage.get();
        }

        LocalizationMessage resolvedMessage = findMessage(normalizedMessageKey, normalizedFallbackLanguage)
                .orElseThrow(() -> new LocalizationMessageNotFoundException(messageKey, language, fallbackLanguage));
        localizationLookupCache.put(lookupCacheKey, resolvedMessage);
        applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_LOOKUPS, "put");
        return resolvedMessage;
    }

    public String getMessageWithFallback(String messageKey, String language, String fallbackLanguage) {
        return findByMessageKeyAndLanguageWithFallback(messageKey, language, fallbackLanguage).getMessageText();
    }

    public LocalizationMessage findByMessageKeyForCurrentLanguageWithFallback(String messageKey) {
        return findByMessageKeyAndLanguageWithFallback(
                messageKey,
                resolveCurrentLanguageOrDefault(),
                RequestLanguageResolver.DEFAULT_LANGUAGE
        );
    }

    public String getCurrentLanguageOrDefault() {
        return resolveCurrentLanguageOrDefault();
    }

    public Map<String, String> getAllMessages(String language) {
        applicationMetrics.recordLocalizationOperation("getAllMessages");
        String normalizedLanguage = normalizeSupportedLanguage(language);
        Cache localizationMessageMapsCache = requireCache(CacheNames.LOCALIZATION_MESSAGE_MAPS);
        @SuppressWarnings("unchecked")
        Map<String, String> cachedMessages = localizationMessageMapsCache.get(normalizedLanguage, Map.class);
        if (cachedMessages != null) {
            applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_MESSAGE_MAPS, "hit");
            return cachedMessages;
        }

        applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_MESSAGE_MAPS, "miss");
        Map<String, String> messagesByKey = new LinkedHashMap<>();
        for (LocalizationMessage message : localizationMessageRepository.findAllByLanguageOrderByMessageKeyAsc(normalizedLanguage)) {
            messagesByKey.put(message.getMessageKey(), message.getMessageText());
        }
        localizationMessageMapsCache.put(normalizedLanguage, messagesByKey);
        applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_MESSAGE_MAPS, "put");
        return messagesByKey;
    }

    public List<LocalizationMessage> findAllByLanguage(String language) {
        applicationMetrics.recordLocalizationOperation("listByLanguage");
        String normalizedLanguage = normalizeSupportedLanguage(language);
        Cache localizationListsCache = requireCache(CacheNames.LOCALIZATION_LISTS);
        @SuppressWarnings("unchecked")
        List<LocalizationMessage> cachedMessages = localizationListsCache.get(normalizedLanguage, List.class);
        if (cachedMessages != null) {
            applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_LISTS, "hit");
            return cachedMessages;
        }

        applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_LISTS, "miss");
        List<LocalizationMessage> messages = localizationMessageRepository.findAllByLanguageOrderByMessageKeyAsc(normalizedLanguage);
        localizationListsCache.put(normalizedLanguage, messages);
        applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_LISTS, "put");
        return messages;
    }

    @Transactional
    public LocalizationMessage create(LocalizationMessageRequest request) {
        userAccountService.requireRole(UserRole.ADMIN, "Localization management requires the ADMIN role.");
        String messageKey = normalizeMessageKey(request.messageKey());
        String language = normalizeSupportedLanguage(request.language());
        validateUniqueMessage(messageKey, language, null);

        LocalizationMessage message = new LocalizationMessage(
                messageKey,
                language,
                request.messageText(),
                request.description()
        );
        LocalizationMessage savedMessage = localizationMessageRepository.saveAndFlush(message);
        evictLocalizationCaches();
        applicationMetrics.recordLocalizationOperation("create");
        auditLogService.record(
                AuditTargetType.LOCALIZATION_MESSAGE,
                savedMessage.getId(),
                AuditAction.CREATE,
                "Created localization message '%s' in language %s."
                        .formatted(savedMessage.getMessageKey(), savedMessage.getLanguage())
        );
        log.info(
                "Created localization message id={} key={} language={}",
                savedMessage.getId(),
                savedMessage.getMessageKey(),
                savedMessage.getLanguage()
        );
        return savedMessage;
    }

    @Transactional
    public LocalizationMessage update(Long id, LocalizationMessageRequest request) {
        userAccountService.requireRole(UserRole.ADMIN, "Localization management requires the ADMIN role.");
        LocalizationMessage message = requireMessage(id);
        String messageKey = normalizeMessageKey(request.messageKey());
        String language = normalizeSupportedLanguage(request.language());
        validateUniqueMessage(messageKey, language, id);

        message.setMessageKey(messageKey);
        message.setLanguage(language);
        message.setMessageText(request.messageText());
        message.setDescription(request.description());

        LocalizationMessage updatedMessage = localizationMessageRepository.saveAndFlush(message);
        evictLocalizationCaches();
        applicationMetrics.recordLocalizationOperation("update");
        auditLogService.record(
                AuditTargetType.LOCALIZATION_MESSAGE,
                updatedMessage.getId(),
                AuditAction.UPDATE,
                "Updated localization message '%s' in language %s."
                        .formatted(updatedMessage.getMessageKey(), updatedMessage.getLanguage())
        );
        log.info(
                "Updated localization message id={} key={} language={}",
                updatedMessage.getId(),
                updatedMessage.getMessageKey(),
                updatedMessage.getLanguage()
        );
        return updatedMessage;
    }

    @Transactional
    public void delete(Long id) {
        userAccountService.requireRole(UserRole.ADMIN, "Localization management requires the ADMIN role.");
        LocalizationMessage message = requireMessage(id);
        localizationMessageRepository.delete(message);
        evictLocalizationCaches();
        applicationMetrics.recordLocalizationOperation("delete");
        auditLogService.record(
                AuditTargetType.LOCALIZATION_MESSAGE,
                id,
                AuditAction.DELETE,
                "Deleted localization message '%s' in language %s."
                        .formatted(message.getMessageKey(), message.getLanguage())
        );
        log.info("Deleted localization message id={} key={} language={}", id, message.getMessageKey(), message.getLanguage());
    }

    private Optional<LocalizationMessage> findMessage(String messageKey, String language) {
        return localizationMessageRepository.findByMessageKeyAndLanguage(messageKey, language);
    }

    private LocalizationMessage requireMessage(Long id) {
        return localizationMessageRepository.findById(id)
                .orElseThrow(() -> new LocalizationMessageNotFoundException(id));
    }

    private String normalizeMessageKey(String messageKey) {
        if (messageKey == null || messageKey.isBlank()) {
            throw new InvalidRequestException("messageKey is required.");
        }

        String normalizedMessageKey = messageKey.trim();
        if (!MESSAGE_KEY_PATTERN.matcher(normalizedMessageKey).matches()) {
            throw new InvalidRequestException("messageKey must match ^[a-z0-9._-]+$.");
        }
        return normalizedMessageKey;
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            throw new InvalidRequestException("language is required.");
        }

        String normalizedLanguage = language.trim().toLowerCase(Locale.ROOT);
        if (!LANGUAGE_PATTERN.matcher(normalizedLanguage).matches()) {
            throw new InvalidRequestException("language must be a two-letter ISO 639-1 code.");
        }
        return normalizedLanguage;
    }

    private String normalizeSupportedLanguage(String language) {
        String normalizedLanguage = normalizeLanguage(language);
        if (!SupportedLanguages.isSupported(normalizedLanguage)) {
            throw new InvalidRequestException("language must be one of: %s.".formatted(SupportedLanguages.description()));
        }
        return normalizedLanguage;
    }

    private Pageable createEffectivePageable(Pageable pageable) {
        Sort effectiveSort = pageable.getSort().isSorted() ? normalizeSort(pageable.getSort()) : Sort.by(Sort.Order.asc("id"));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), effectiveSort);
    }

    private Sort normalizeSort(Sort sort) {
        for (Sort.Order order : sort) {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new InvalidRequestException(
                        "Sort field '%s' is not supported. Use one of: id, messageKey, language, createdAt, updatedAt."
                                .formatted(order.getProperty())
                );
            }
        }
        return sort;
    }

    private void validateUniqueMessage(String messageKey, String language, Long id) {
        boolean exists = id == null
                ? localizationMessageRepository.existsByMessageKeyAndLanguage(messageKey, language)
                : localizationMessageRepository.existsByMessageKeyAndLanguageAndIdNot(messageKey, language, id);
        if (exists) {
            throw new DuplicateLocalizationMessageException(messageKey, language);
        }
    }

    private void evictLocalizationCaches() {
        requireCache(CacheNames.LOCALIZATION_LOOKUPS).clear();
        applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_LOOKUPS, "evict");
        requireCache(CacheNames.LOCALIZATION_LISTS).clear();
        applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_LISTS, "evict");
        requireCache(CacheNames.LOCALIZATION_MESSAGE_MAPS).clear();
        applicationMetrics.recordCacheEvent(CacheNames.LOCALIZATION_MESSAGE_MAPS, "evict");
    }

    private Cache requireCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache '%s' is not configured.".formatted(cacheName));
        }
        return cache;
    }

    private String resolveCurrentLanguageOrDefault() {
        return localizationContext.getCurrentLanguage()
                .or(() -> userAccountService.findCurrentUserPreferredLanguage())
                .orElse(RequestLanguageResolver.DEFAULT_LANGUAGE);
    }
}
