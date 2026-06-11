package team.jit.technicalinterviewdemo.business.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.business.audit.AuditAction;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.audit.AuditTargetType;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminUserManagementService {

    private final CurrentUserAccountService currentUserAccountService;
    private final UserAccountRepository userAccountRepository;
    private final ApplicationMetrics applicationMetrics;
    private final AuditLogService auditLogService;

    public List<AdminUserAccountResponse> listUsers() {
        currentUserAccountService.requireRole(UserRole.ADMIN, "User management requires the ADMIN role.");
        applicationMetrics.recordUserOperation("listAdminUsers");
        return userAccountRepository.findAllByOrderByIdAsc().stream()
                .map(AdminUserAccountResponse::from)
                .toList();
    }

    @Transactional
    public AdminUserAccountResponse replaceRoles(Long userId, AdminUserRoleUpdateRequest request) {
        currentUserAccountService.requireRole(UserRole.ADMIN, "User management requires the ADMIN role.");
        UserAccount currentAdmin = currentUserAccountService.getCurrentUserOrSynchronize();
        UserAccount targetUser =
                userAccountRepository.findById(userId).orElseThrow(() -> new UserAccountNotFoundException(userId));
        List<String> previousRoles =
                targetUser.getRoles().stream().map(Enum::name).sorted().toList();

        try {
            targetUser.replaceManagedRoleGrants(request.requestedRoles(), currentAdmin, request.reason());
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException(exception.getMessage(), exception);
        }

        UserAccount updatedUser = userAccountRepository.saveAndFlush(targetUser);
        List<String> updatedRoles =
                updatedUser.getRoles().stream().map(Enum::name).sorted().toList();
        applicationMetrics.recordUserOperation("replaceManagedRoles");
        auditLogService.record(
                AuditTargetType.USER_ACCOUNT,
                updatedUser.getId(),
                AuditAction.UPDATE,
                "Replaced managed roles for user '%s'.".formatted(updatedUser.getExternalLogin()),
                Map.of(
                        "targetProvider",
                        updatedUser.getProvider(),
                        "targetLogin",
                        updatedUser.getExternalLogin(),
                        "previousRoles",
                        previousRoles,
                        "roles",
                        updatedRoles,
                        "reason",
                        request.reason()));
        log.info(
                "Replaced managed role grants userId={} roles={} grantedByUserId={}",
                updatedUser.getId(),
                updatedUser.getRoles(),
                currentAdmin.getId());
        return AdminUserAccountResponse.from(updatedUser);
    }

    @Transactional
    public AdminUserAccountResponse replaceStatus(Long userId, AdminUserAccountStatusUpdateRequest request) {
        currentUserAccountService.requireRole(UserRole.ADMIN, "User management requires the ADMIN role.");
        UserAccount currentAdmin = currentUserAccountService.getCurrentUserOrSynchronize();
        UserAccount targetUser =
                userAccountRepository.findById(userId).orElseThrow(() -> new UserAccountNotFoundException(userId));
        if (targetUser.getId().equals(currentAdmin.getId())) {
            throw new InvalidRequestException("Changing the status of your own account is not allowed.");
        }

        UserAccountStatus previousStatus = UserAccountStatus.of(targetUser);
        if (previousStatus == request.status()) {
            return AdminUserAccountResponse.from(targetUser);
        }

        if (request.status() == UserAccountStatus.BLOCKED) {
            targetUser.block(currentAdmin, request.reason());
        } else {
            targetUser.unblock();
        }

        UserAccount updatedUser = userAccountRepository.saveAndFlush(targetUser);
        applicationMetrics.recordUserOperation("replaceAccountStatus");
        auditLogService.record(
                AuditTargetType.USER_ACCOUNT,
                updatedUser.getId(),
                AuditAction.UPDATE,
                "Replaced account status for user '%s'.".formatted(updatedUser.getExternalLogin()),
                Map.of(
                        "targetProvider",
                        updatedUser.getProvider(),
                        "targetLogin",
                        updatedUser.getExternalLogin(),
                        "previousStatus",
                        previousStatus.name(),
                        "status",
                        request.status().name(),
                        "reason",
                        request.reason()));
        log.info(
                "Replaced account status userId={} status={} changedByUserId={}",
                updatedUser.getId(),
                request.status(),
                currentAdmin.getId());
        return AdminUserAccountResponse.from(updatedUser);
    }
}
