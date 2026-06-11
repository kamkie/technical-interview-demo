package team.jit.technicalinterviewdemo.business.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
public class UserDataInitializer {

    private static final int DEMO_USER_COUNT = 500;
    private static final Instant FIRST_DEMO_LOGIN_AT = Instant.parse("2024-01-01T00:00:00Z");
    private static final String DEMO_PROVIDER = "github";
    private static final int BLOCKED_DEMO_USER_INDEX = 2;
    private static final Instant BLOCKED_DEMO_USER_BLOCKED_AT = Instant.parse("2024-01-15T00:00:00Z");
    private static final String BLOCKED_DEMO_USER_REASON = "Seeded demo blocked account.";

    @Bean
    @Order(30)
    CommandLineRunner seedUsers(
            UserAccountRepository userAccountRepository, BootstrapSettingsProperties bootstrapSettingsProperties) {
        return args -> {
            if (!bootstrapSettingsProperties.getSeed().isDemoData()) {
                log.info("Skipping demo user bootstrap because app.bootstrap.seed.demo-data is disabled.");
                return;
            }

            List<UserAccount> users = defaultUsers().stream()
                    .filter(seedUser -> !userAccountRepository.existsByProviderAndExternalLogin(
                            seedUser.provider(), seedUser.externalLogin()))
                    .map(UserDataInitializer::toUserAccount)
                    .toList();
            if (users.isEmpty()) {
                return;
            }

            userAccountRepository.saveAll(users).forEach(UserDataInitializer::logSavedUser);
        };
    }

    static List<SeedUser> defaultUsers() {
        List<SeedUser> users = new ArrayList<>(DEMO_USER_COUNT);
        for (int index = 1; index <= DEMO_USER_COUNT; index++) {
            users.add(generatedUser(index));
        }
        return List.copyOf(users);
    }

    private static SeedUser generatedUser(int index) {
        String sequence = "%03d".formatted(index);
        boolean blocked = index == BLOCKED_DEMO_USER_INDEX;
        return new SeedUser(
                DEMO_PROVIDER,
                "demo-user-" + sequence,
                "Demo User " + sequence,
                "demo-user-" + sequence + "@example.test",
                FIRST_DEMO_LOGIN_AT.plus(index - 1L, ChronoUnit.HOURS),
                blocked ? BLOCKED_DEMO_USER_BLOCKED_AT : null,
                blocked ? BLOCKED_DEMO_USER_REASON : null);
    }

    private static UserAccount toUserAccount(SeedUser seedUser) {
        UserAccount userAccount = new UserAccount(
                seedUser.provider(),
                seedUser.externalLogin(),
                seedUser.displayName(),
                seedUser.email(),
                null,
                seedUser.lastLoginAt(),
                Set.of(UserRole.USER));
        if (seedUser.blockedAt() != null) {
            userAccount.block(seedUser.blockedAt(), null, seedUser.blockedReason());
        }
        return userAccount;
    }

    private static void logSavedUser(UserAccount savedUser) {
        log.info(
                "Seeded user id={} provider={} login={}",
                savedUser.getId(),
                savedUser.getProvider(),
                savedUser.getExternalLogin());
    }

    record SeedUser(
            String provider,
            String externalLogin,
            String displayName,
            String email,
            Instant lastLoginAt,
            Instant blockedAt,
            String blockedReason) {}
}
