package team.jit.technicalinterviewdemo.business.localization;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocalizationRepository extends JpaRepository<Localization, Long> {

    Page<Localization> findAllByMessageKey(String messageKey, Pageable pageable);

    Page<Localization> findAllByLanguage(String language, Pageable pageable);

    Page<Localization> findAllByMessageKeyAndLanguage(String messageKey, String language, Pageable pageable);

    Optional<Localization> findByMessageKeyAndLanguage(String messageKey, String language);

    List<Localization> findAllByLanguageOrderByMessageKeyAsc(String language);

    boolean existsByMessageKeyAndLanguage(String messageKey, String language);

    boolean existsByMessageKeyAndLanguageAndIdNot(String messageKey, String language, Long id);
}
