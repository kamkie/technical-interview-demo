package team.jit.technicalinterviewdemo.business.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminOauthUser;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.oauthUser;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import team.jit.technicalinterviewdemo.business.book.Book;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.localization.LocalizationMessage;
import team.jit.technicalinterviewdemo.business.localization.LocalizationMessageRepository;
import team.jit.technicalinterviewdemo.business.user.UserAccountRepository;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
class AuditLogIntegrationTests extends AbstractMockMvcIntegrationTest {

    private static final String EXISTING_LOCALIZATION_KEY = "audit.localization.seed";
    private static final String CREATED_LOCALIZATION_KEY = "audit.localization.created";

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LocalizationMessageRepository localizationMessageRepository;

    private Book cleanCode;
    private LocalizationMessage bookNotFoundEn;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        userAccountRepository.deleteAll();
        bookRepository.deleteAll();
        localizationMessageRepository.findByMessageKeyAndLanguage(EXISTING_LOCALIZATION_KEY, "en")
                .ifPresent(localizationMessageRepository::delete);
        localizationMessageRepository.findByMessageKeyAndLanguage(CREATED_LOCALIZATION_KEY, "fr")
                .ifPresent(localizationMessageRepository::delete);

        cleanCode = bookRepository.saveAndFlush(new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008));
        bookNotFoundEn = localizationMessageRepository.saveAndFlush(new LocalizationMessage(
                EXISTING_LOCALIZATION_KEY,
                "en",
                "Seeded audit message.",
                "English message used by audit logging integration tests."
        ));
    }

    @Test
    void bookCreateUpdateAndDeleteProduceAuditLogsWithActor() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(oauthUser("reader-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Spring in Action",
                                  "author": "Craig Walls",
                                  "isbn": "9781617297571",
                                  "publicationYear": 2022
                                }
                                """))
                .andExpect(status().isCreated());

        Book createdBook = bookRepository.findAll().stream()
                .filter(book -> "9781617297571".equals(book.getIsbn()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(put("/api/books/{id}", cleanCode.getId())
                        .with(oauthUser("reader-user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code Second Edition",
                                  "author": "Robert C. Martin",
                                  "version": %d,
                                  "publicationYear": 2026
                                }
                                """.formatted(cleanCode.getVersion())))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/books/{id}", cleanCode.getId())
                        .with(oauthUser("reader-user")))
                .andExpect(status().isNoContent());

        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByIdAsc();

        assertThat(auditLogs).hasSize(3);
        assertThat(auditLogs).extracting(AuditLog::getTargetType)
                .containsExactly(AuditTargetType.BOOK, AuditTargetType.BOOK, AuditTargetType.BOOK);
        assertThat(auditLogs).extracting(AuditLog::getAction)
                .containsExactly(AuditAction.CREATE, AuditAction.UPDATE, AuditAction.DELETE);
        assertThat(auditLogs).extracting(AuditLog::getTargetId)
                .containsExactly(createdBook.getId(), cleanCode.getId(), cleanCode.getId());
        assertThat(auditLogs).extracting(AuditLog::getActorLogin)
                .containsOnly("reader-user");
        assertThat(auditLogs).allSatisfy(auditLog -> {
            assertThat(auditLog.getActorUser()).isNotNull();
            assertThat(auditLog.getCreatedAt()).isNotNull();
            assertThat(auditLog.getSummary()).isNotBlank();
        });
    }

    @Test
    void localizationCreateUpdateAndDeleteProduceAuditLogsWithAdminActor() throws Exception {
        mockMvc.perform(post("/api/localization-messages")
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "%s",
                                  "language": "fr",
                                  "messageText": "Le livre a ete cree.",
                                  "description": "French success message for new books."
                                }
                                """.formatted(CREATED_LOCALIZATION_KEY)))
                .andExpect(status().isCreated());

        LocalizationMessage createdMessage = localizationMessageRepository
                .findByMessageKeyAndLanguage(CREATED_LOCALIZATION_KEY, "fr")
                .orElseThrow();

        mockMvc.perform(put("/api/localization-messages/{id}", bookNotFoundEn.getId())
                        .with(adminOauthUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messageKey": "audit.localization.updated",
                                  "language": "fr",
                                  "messageText": "Le livre demande est introuvable.",
                                  "description": "French message for missing book errors."
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/localization-messages/{id}", bookNotFoundEn.getId())
                        .with(adminOauthUser()))
                .andExpect(status().isNoContent());

        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByIdAsc();

        assertThat(auditLogs).hasSize(3);
        assertThat(auditLogs).extracting(AuditLog::getTargetType)
                .containsExactly(
                        AuditTargetType.LOCALIZATION_MESSAGE,
                        AuditTargetType.LOCALIZATION_MESSAGE,
                        AuditTargetType.LOCALIZATION_MESSAGE
                );
        assertThat(auditLogs).extracting(AuditLog::getAction)
                .containsExactly(AuditAction.CREATE, AuditAction.UPDATE, AuditAction.DELETE);
        assertThat(auditLogs).extracting(AuditLog::getTargetId)
                .containsExactly(createdMessage.getId(), bookNotFoundEn.getId(), bookNotFoundEn.getId());
        assertThat(auditLogs).extracting(AuditLog::getActorLogin)
                .containsOnly("admin-user");
        assertThat(auditLogs).allSatisfy(auditLog -> {
            assertThat(auditLog.getActorUser()).isNotNull();
            assertThat(auditLog.getCreatedAt()).isNotNull();
            assertThat(auditLog.getSummary()).isNotBlank();
        });
    }
}

