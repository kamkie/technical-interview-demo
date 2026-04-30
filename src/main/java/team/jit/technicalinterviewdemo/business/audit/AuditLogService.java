package team.jit.technicalinterviewdemo.business.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.business.user.UserAccount;
import team.jit.technicalinterviewdemo.business.user.UserAccountService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserAccountService userAccountService;

    @Transactional
    public AuditLog record(AuditTargetType targetType, Long targetId, AuditAction action, String summary) {
        UserAccount actorUser = userAccountService.findCurrentUser().orElse(null);
        String actorLogin = actorUser == null ? "system" : actorUser.getExternalLogin();
        AuditLog auditLog = new AuditLog(targetType, targetId, action, actorUser, actorLogin, summary);
        AuditLog savedAuditLog = auditLogRepository.save(auditLog);
        log.info(
                "Recorded audit log id={} targetType={} targetId={} action={} actorLogin={}",
                savedAuditLog.getId(),
                savedAuditLog.getTargetType(),
                savedAuditLog.getTargetId(),
                savedAuditLog.getAction(),
                savedAuditLog.getActorLogin()
        );
        return savedAuditLog;
    }
}
