package team.jit.technicalinterviewdemo.business.audit;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.business.user.CurrentUserAccountService;
import team.jit.technicalinterviewdemo.business.user.UserRole;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuditLogQueryService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "targetType", "targetId", "action", "actorLogin", "createdAt");

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserAccountService currentUserAccountService;

    public Page<AuditLog> findAll(Pageable pageable, AuditTargetType targetType, AuditAction action, String actorLogin) {
        currentUserAccountService.requireRole(UserRole.ADMIN, "Audit log review requires the ADMIN role.");
        Pageable effectivePageable = createEffectivePageable(pageable);
        Specification<AuditLog> specification = Specification.where(hasTargetType(targetType))
                .and(hasAction(action))
                .and(hasActorLogin(actorLogin));
        return auditLogRepository.findAll(specification, effectivePageable);
    }

    private Specification<AuditLog> hasTargetType(AuditTargetType targetType) {
        return (root, query, criteriaBuilder) -> targetType == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("targetType"), targetType);
    }

    private Specification<AuditLog> hasAction(AuditAction action) {
        return (root, query, criteriaBuilder) -> action == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("action"), action);
    }

    private Specification<AuditLog> hasActorLogin(String actorLogin) {
        if (actorLogin == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }

        String normalizedActorLogin = actorLogin.trim();
        if (normalizedActorLogin.isEmpty()) {
            throw new InvalidRequestException("actorLogin is required when the filter is provided.");
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("actorLogin"), normalizedActorLogin);
    }

    private Pageable createEffectivePageable(Pageable pageable) {
        Sort effectiveSort = pageable.getSort().isSorted()
                ? normalizeSort(pageable.getSort())
                : Sort.by(Sort.Order.desc("id"));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), effectiveSort);
    }

    private Sort normalizeSort(Sort sort) {
        for (Sort.Order order : sort) {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new InvalidRequestException(
                        "Sort field '%s' is not supported. Use one of: id, targetType, targetId, action, actorLogin, createdAt."
                                .formatted(order.getProperty())
                );
            }
        }
        return sort;
    }
}
