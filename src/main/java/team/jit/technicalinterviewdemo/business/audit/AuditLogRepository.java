package team.jit.technicalinterviewdemo.business.audit;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @EntityGraph(attributePaths = "actorUser")
    List<AuditLog> findAllByOrderByIdAsc();
}
