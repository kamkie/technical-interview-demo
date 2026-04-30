package team.jit.technicalinterviewdemo.business.localization;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalizationMessageRepository extends JpaRepository<LocalizationMessage, Long> {

    Optional<LocalizationMessage> findByMessageKeyAndLanguage(String messageKey, String language);

    List<LocalizationMessage> findAllByLanguageOrderByMessageKeyAsc(String language);

    boolean existsByMessageKeyAndLanguage(String messageKey, String language);

    boolean existsByMessageKeyAndLanguageAndIdNot(String messageKey, String language, Long id);
}
