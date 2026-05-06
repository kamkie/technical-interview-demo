package team.jit.technicalinterviewdemo.business.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @EntityGraph(attributePaths = {"roleGrants", "roleGrants.grantedByUser"})
    Optional<UserAccount> findByProviderAndExternalLogin(String provider, String externalLogin);

    @Override
    @EntityGraph(attributePaths = {"roleGrants", "roleGrants.grantedByUser"})
    Optional<UserAccount> findById(Long id);

    @EntityGraph(attributePaths = {"roleGrants", "roleGrants.grantedByUser"})
    List<UserAccount> findAllByOrderByIdAsc();

    @Query("""
        select count(distinct userAccount)
        from UserAccount userAccount
        join userAccount.roleGrants grant
        where grant.role = :role
        """)
    long countByRole(@Param("role") UserRole role);
}
