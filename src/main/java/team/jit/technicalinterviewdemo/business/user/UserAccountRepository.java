package team.jit.technicalinterviewdemo.business.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<UserAccount> findByProviderAndExternalLogin(String provider, String externalLogin);

    @Override
    @EntityGraph(attributePaths = "roles")
    Optional<UserAccount> findById(Long id);

    @Query("""
            select count(distinct userAccount)
            from UserAccount userAccount
            join userAccount.roles role
            where role = :role
            """)
    long countByRole(@Param("role") UserRole role);
}
