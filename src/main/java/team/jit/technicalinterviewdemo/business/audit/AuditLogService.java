package team.jit.technicalinterviewdemo.business.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserAccount;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserAccountService currentUserAccountService;

    @Transactional
    public AuditLog record(AuditTargetType targetType, Long targetId, AuditAction action, String summary) {
        return record(targetType, targetId, action, summary, Map.of());
    }

    @Transactional
    public AuditLog record(
        AuditTargetType targetType, Long targetId, AuditAction action, String summary, Map<String, Object> details
    ) {
        UserAccount actorUser = currentUserAccountService.findCurrentUser().orElse(null);
        String actorLogin = actorUser == null ? "system" : actorUser.getExternalLogin();
        return recordWithActor(targetType, targetId, action, actorUser, actorLogin, summary, details);
    }

    @Transactional
    public AuditLog recordWithActor(
        AuditTargetType targetType, Long targetId, AuditAction action, UserAccount actorUser, String actorLogin, String summary, Map<String, Object> details
    ) {
        AuditLog auditLog = new AuditLog(
            targetType, targetId, action, actorUser, actorLogin, summary, normalizeDetails(details)
        );
        AuditLog savedAuditLog = auditLogRepository.save(auditLog);
        log.info(
            "Recorded audit log id={} targetType={} targetId={} action={} actorLogin={}", savedAuditLog.getId(), savedAuditLog.getTargetType(), savedAuditLog.getTargetId(), savedAuditLog.getAction(), savedAuditLog.getActorLogin()
        );
        return savedAuditLog;
    }

    private Map<String, Object> normalizeDetails(Map<String, Object> details) {
        if (details == null || details.isEmpty()) {
            return Map.of();
        }
        return new LinkedHashMap<>(details);
    }
}
