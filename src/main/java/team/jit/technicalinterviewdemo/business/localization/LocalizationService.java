package team.jit.technicalinterviewdemo.business.localization;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
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
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserRole;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.cache.CacheNames;
import team.jit.technicalinterviewdemo.technical.localization.LocalizationContext;
import team.jit.technicalinterviewdemo.technical.localization.RequestLanguageResolver;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocalizationService {

    private static final Pattern MESSAGE_KEY_PATTERN = Pattern.compile("^[a-z0-9._-]+$");
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("^[a-zA-Z]{2}$");
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "messageKey", "language", "createdAt", "updatedAt");

    private final LocalizationRepository localizationRepository;
    private final LocalizationContext localizationContext;
    private final CacheManager cacheManager;
    private final ApplicationMetrics applicationMetrics;
    private final CurrentUserAccountService currentUserAccountService;
    private final AuditLogService auditLogService;

    public Page<Localization> findAll(Pageable pageable, String messageKey, String language) {
        Pageable effectivePageable = createEffectivePageable(pageable);
        LocalizationFilters filters = normalizeFilters(messageKey, language);

        if (filters.hasMessageKey() && filters.hasLanguage()) {
            applicationMetrics.recordLocalizationOperation("listFiltered");
            return localizationRepository.findAllByMessageKeyAndLanguage(filters.messageKey(), filters.language(), effectivePageable);
        }
        if (filters.hasMessageKey()) {
            applicationMetrics.recordLocalizationOperation("listFiltered");
            return localizationRepository.findAllByMessageKey(filters.messageKey(), effectivePageable);
        }
        if (filters.hasLanguage()) {
            applicationMetrics.recordLocalizationOperation("listFiltered");
            return localizationRepository.findAllByLanguage(filters.language(), effectivePageable);
        }

        applicationMetrics.recordLocalizationOperation("list");
        return localizationRepository.findAll(effectivePageable);
    }

    public Localization findById(Long id) {
        applicationMetrics.recordLocalizationOperation("get");
        return localizationRepository.findById(id).orElseThrow(() -> new LocalizationNotFoundException(id));
    }

    public Localization findByMessageKeyAndLanguage(String messageKey, String language) {
        applicationMetrics.recordLocalizationOperation("lookupExact");
        return findMessage(normalizeMessageKey(messageKey), normalizeSupportedLanguage(language)).orElseThrow(() -> new LocalizationNotFoundException(messageKey, language));
    }

    public String getMessage(String messageKey, String language) {
        return findByMessageKeyAndLanguage(messageKey, language).getMessageText();
    }

    public Localization findByMessageKeyAndLanguageWithFallback(String messageKey, String language, String fallbackLanguage) {
        LocalizationLookupRequest lookupRequest = normalizeLookupRequest(messageKey, language, fallbackLanguage);
        applicationMetrics.recordLocalizationOperation("lookupWithFallback");
        return getCachedLookup(lookupRequest);
    }

    public String getMessageWithFallback(String messageKey, String language, String fallbackLanguage) {
        return findByMessageKeyAndLanguageWithFallback(messageKey, language, fallbackLanguage).getMessageText();
    }

    public Localization findByMessageKeyForCurrentLanguageWithFallback(String messageKey) {
        return findByMessageKeyAndLanguageWithFallback(
                messageKey, localizationContext.resolveCurrentLanguageOrDefault(), RequestLanguageResolver.DEFAULT_LANGUAGE
        );
    }

    public Map<String, String> getAllMessages(String language) {
        applicationMetrics.recordLocalizationOperation("getAllMessages");
        return getCachedMessageMap(normalizeSupportedLanguage(language));
    }

    public List<Localization> findAllByLanguage(String language) {
        applicationMetrics.recordLocalizationOperation("listByLanguage");
        return getCachedLocalizationList(normalizeSupportedLanguage(language));
    }

    @Transactional
    public Localization create(LocalizationRequest request) {
        currentUserAccountService.requireRole(UserRole.ADMIN, "Localization management requires the ADMIN role.");
        String messageKey = normalizeMessageKey(request.messageKey());
        String language = normalizeSupportedLanguage(request.language());
        validateUniqueMessage(messageKey, language, null);

        Localization message = new Localization(
                messageKey, language, request.messageText(), request.description()
        );
        Localization savedMessage = localizationRepository.saveAndFlush(message);
        evictLocalizationCaches();
        applicationMetrics.recordLocalizationOperation("create");
        auditLogService.record(
                AuditTargetType.LOCALIZATION_MESSAGE, savedMessage.getId(), AuditAction.CREATE, "Created localization message '%s' in language %s.".formatted(savedMessage.getMessageKey(), savedMessage.getLanguage()), auditDetails(savedMessage)
        );
        log.info(
                "Created localization message id={} key={} language={}", savedMessage.getId(), savedMessage.getMessageKey(), savedMessage.getLanguage()
        );
        return savedMessage;
    }

    @Transactional
    public Localization update(Long id, LocalizationRequest request) {
        currentUserAccountService.requireRole(UserRole.ADMIN, "Localization management requires the ADMIN role.");
        Localization message = requireMessage(id);
        String messageKey = normalizeMessageKey(request.messageKey());
        String language = normalizeSupportedLanguage(request.language());
        validateUniqueMessage(messageKey, language, id);

        message.setMessageKey(messageKey);
        message.setLanguage(language);
        message.setMessageText(request.messageText());
        message.setDescription(request.description());

        Localization updatedMessage = localizationRepository.saveAndFlush(message);
        evictLocalizationCaches();
        applicationMetrics.recordLocalizationOperation("update");
        auditLogService.record(
                AuditTargetType.LOCALIZATION_MESSAGE, updatedMessage.getId(), AuditAction.UPDATE, "Updated localization message '%s' in language %s.".formatted(updatedMessage.getMessageKey(), updatedMessage.getLanguage()), auditDetails(updatedMessage)
        );
        log.info(
                "Updated localization message id={} key={} language={}", updatedMessage.getId(), updatedMessage.getMessageKey(), updatedMessage.getLanguage()
        );
        return updatedMessage;
    }

    @Transactional
    public void delete(Long id) {
        currentUserAccountService.requireRole(UserRole.ADMIN, "Localization management requires the ADMIN role.");
        Localization message = requireMessage(id);
        localizationRepository.delete(message);
        evictLocalizationCaches();
        applicationMetrics.recordLocalizationOperation("delete");
        auditLogService.record(
                AuditTargetType.LOCALIZATION_MESSAGE, id, AuditAction.DELETE, "Deleted localization message '%s' in language %s.".formatted(message.getMessageKey(), message.getLanguage()), auditDetails(message)
        );
        log.info("Deleted localization message id={} key={} language={}", id, message.getMessageKey(), message.getLanguage());
    }

    private Optional<Localization> findMessage(String messageKey, String language) {
        return localizationRepository.findByMessageKeyAndLanguage(messageKey, language);
    }

    private Localization resolveMessageWithFallback(LocalizationLookupRequest lookupRequest) {
        return findMessage(lookupRequest.normalizedMessageKey(), lookupRequest.normalizedLanguage()).or(() -> findMessage(lookupRequest.normalizedMessageKey(), lookupRequest.normalizedFallbackLanguage())).orElseThrow(() -> new LocalizationNotFoundException(
                lookupRequest.requestedMessageKey(), lookupRequest.requestedLanguage(), lookupRequest.requestedFallbackLanguage()
        ));
    }

    private Localization requireMessage(Long id) {
        return localizationRepository.findById(id).orElseThrow(() -> new LocalizationNotFoundException(id));
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

    private String normalizeOptionalMessageKey(String messageKey) {
        return messageKey == null ? null : normalizeMessageKey(messageKey);
    }

    private String normalizeOptionalSupportedLanguage(String language) {
        return language == null ? null : normalizeSupportedLanguage(language);
    }

    private LocalizationFilters normalizeFilters(String messageKey, String language) {
        return new LocalizationFilters(
                normalizeOptionalMessageKey(messageKey), normalizeOptionalSupportedLanguage(language)
        );
    }

    private LocalizationLookupRequest normalizeLookupRequest(String messageKey, String language, String fallbackLanguage) {
        return new LocalizationLookupRequest(
                messageKey, language, fallbackLanguage, normalizeMessageKey(messageKey), normalizeLanguage(language), normalizeSupportedLanguage(fallbackLanguage)
        );
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
                        "Sort field '%s' is not supported. Use one of: id, messageKey, language, createdAt, updatedAt.".formatted(order.getProperty())
                );
            }
        }
        return sort;
    }

    private void validateUniqueMessage(String messageKey, String language, Long id) {
        boolean exists = id == null ? localizationRepository.existsByMessageKeyAndLanguage(messageKey, language) : localizationRepository.existsByMessageKeyAndLanguageAndIdNot(messageKey, language, id);
        if (exists) {
            throw new DuplicateLocalizationException(messageKey, language);
        }
    }

    private void evictLocalizationCaches() {
        clearCache(CacheNames.LOCALIZATION_LOOKUPS);
        clearCache(CacheNames.LOCALIZATION_LISTS);
        clearCache(CacheNames.LOCALIZATION_MESSAGE_MAPS);
    }

    private Cache requireCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException("Cache '%s' is not configured.".formatted(cacheName));
        }
        return cache;
    }

    private void clearCache(String cacheName) {
        requireCache(cacheName).clear();
        applicationMetrics.recordCacheEvent(cacheName, "evict");
    }

    private Localization getCachedLookup(LocalizationLookupRequest lookupRequest) {
        return getCachedValue(
                CacheNames.LOCALIZATION_LOOKUPS, lookupRequest.cacheKey(), Localization.class, () -> resolveMessageWithFallback(lookupRequest)
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getCachedMessageMap(String normalizedLanguage) {
        return (Map<String, String>) getCachedSupportedLanguageValue(
                CacheNames.LOCALIZATION_MESSAGE_MAPS, normalizedLanguage, Map.class, () -> loadMessagesByKey(normalizedLanguage)
        );
    }

    @SuppressWarnings("unchecked")
    private List<Localization> getCachedLocalizationList(String normalizedLanguage) {
        return (List<Localization>) getCachedSupportedLanguageValue(
                CacheNames.LOCALIZATION_LISTS, normalizedLanguage, List.class, () -> loadLocalizationsByLanguage(normalizedLanguage)
        );
    }

    private Map<String, String> loadMessagesByKey(String normalizedLanguage) {
        Map<String, String> messagesByKey = new LinkedHashMap<>();
        for (Localization message : loadLocalizationsByLanguage(normalizedLanguage)) {
            messagesByKey.put(message.getMessageKey(), message.getMessageText());
        }
        return messagesByKey;
    }

    private List<Localization> loadLocalizationsByLanguage(String normalizedLanguage) {
        return localizationRepository.findAllByLanguageOrderByMessageKeyAsc(normalizedLanguage);
    }

    private <T> T getCachedSupportedLanguageValue(
                                                  String cacheName, String normalizedLanguage, Class<T> valueType, Supplier<T> valueLoader
    ) {
        return getCachedValue(cacheName, normalizedLanguage, valueType, valueLoader);
    }

    private <T> T getCachedValue(String cacheName, Object cacheKey, Class<T> valueType, Supplier<T> valueLoader) {
        Cache cache = requireCache(cacheName);
        T cachedValue = cache.get(cacheKey, valueType);
        if (cachedValue != null) {
            applicationMetrics.recordCacheEvent(cacheName, "hit");
            return cachedValue;
        }

        applicationMetrics.recordCacheEvent(cacheName, "miss");
        T loadedValue = valueLoader.get();
        cache.put(cacheKey, loadedValue);
        applicationMetrics.recordCacheEvent(cacheName, "put");
        return loadedValue;
    }

    private Map<String, Object> auditDetails(Localization message) {
        return Map.of(
                "messageKey", message.getMessageKey(), "language", message.getLanguage()
        );
    }

    private record LocalizationFilters(String messageKey, String language) {

        private boolean hasMessageKey() {
            return messageKey != null;
        }

        private boolean hasLanguage() {
            return language != null;
        }
    }

    private record LocalizationLookupRequest(
                                             String requestedMessageKey,
                                             String requestedLanguage,
                                             String requestedFallbackLanguage,
                                             String normalizedMessageKey,
                                             String normalizedLanguage,
                                             String normalizedFallbackLanguage
    ) {

        private String cacheKey() {
            return "%s::%s::%s".formatted(
                    normalizedMessageKey, normalizedLanguage, normalizedFallbackLanguage
            );
        }
    }
}
