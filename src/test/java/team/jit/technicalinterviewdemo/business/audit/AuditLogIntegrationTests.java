package team.jit.technicalinterviewdemo.business.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import team.jit.technicalinterviewdemo.business.book.Book;
import team.jit.technicalinterviewdemo.business.book.BookRepository;
import team.jit.technicalinterviewdemo.business.category.Category;
import team.jit.technicalinterviewdemo.business.category.CategoryRepository;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRepository;
import team.jit.technicalinterviewdemo.business.user.UserAccountRepository;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;
import team.jit.technicalinterviewdemo.testing.SecurityTestSupport.BrowserSession;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.adminBrowserSession;
import static team.jit.technicalinterviewdemo.testing.SecurityTestSupport.authenticatedBrowserSession;

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
    private CategoryRepository categoryRepository;

    @Autowired
    private LocalizationRepository localizationMessageRepository;

    @Autowired
    private JdbcIndexedSessionRepository sessionRepository;

    private Book cleanCode;
    private Localization bookNotFoundEn;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        userAccountRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        localizationMessageRepository.findByMessageKeyAndLanguage(EXISTING_LOCALIZATION_KEY, "en").ifPresent(localizationMessageRepository::delete);
        localizationMessageRepository.findByMessageKeyAndLanguage(CREATED_LOCALIZATION_KEY, "fr").ifPresent(localizationMessageRepository::delete);

        cleanCode = bookRepository.saveAndFlush(new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008));
        bookNotFoundEn = localizationMessageRepository.saveAndFlush(new Localization(
            EXISTING_LOCALIZATION_KEY, "en", "Seeded audit message.", "English message used by audit logging integration tests."
        ));
    }

    @Test
    void bookCreateUpdateAndDeleteProduceAuditLogsWithActor() throws Exception {
        BrowserSession readerSession = readerSession();

        mockMvc.perform(post("/api/books").with(readerSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "title": "Spring in Action",
              "author": "Craig Walls",
              "isbn": "9781617297571",
              "publicationYear": 2022
            }
            """)).andExpect(status().isCreated());

        Book createdBook = bookRepository.findAll().stream().filter(book -> "9781617297571".equals(book.getIsbn())).findFirst().orElseThrow();

        mockMvc.perform(put("/api/books/{id}", cleanCode.getId()).with(readerSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "title": "Clean Code Second Edition",
              "author": "Robert C. Martin",
              "version": %d,
              "publicationYear": 2026
            }
            """.formatted(cleanCode.getVersion()))).andExpect(status().isOk());

        mockMvc.perform(delete("/api/books/{id}", cleanCode.getId()).with(readerSession.unsafeWrite())).andExpect(status().isNoContent());

        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByIdAsc();

        assertThat(auditLogs).hasSize(3);
        assertThat(auditLogs).extracting(AuditLog::getTargetType).containsExactly(AuditTargetType.BOOK, AuditTargetType.BOOK, AuditTargetType.BOOK);
        assertThat(auditLogs).extracting(AuditLog::getAction).containsExactly(AuditAction.CREATE, AuditAction.UPDATE, AuditAction.DELETE);
        assertThat(auditLogs).extracting(AuditLog::getTargetId).containsExactly(createdBook.getId(), cleanCode.getId(), cleanCode.getId());
        assertThat(auditLogs).extracting(AuditLog::getActorLogin).containsOnly("reader-user");
        assertThat(auditLogs).allSatisfy(auditLog -> {
            assertThat(auditLog.getActorUser()).isNotNull();
            assertThat(auditLog.getCreatedAt()).isNotNull();
            assertThat(auditLog.getSummary()).isNotBlank();
            assertThat(auditLog.getDetails()).isNotEmpty();
        });
    }

    @Test
    void localizationCreateUpdateAndDeleteProduceAuditLogsWithAdminActor() throws Exception {
        BrowserSession adminSession = adminSession();

        mockMvc.perform(post("/api/localizations").with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "messageKey": "%s",
              "language": "fr",
              "messageText": "Le livre a ete cree.",
              "description": "French success message for new books."
            }
            """.formatted(CREATED_LOCALIZATION_KEY))).andExpect(status().isCreated());

        Localization createdMessage = localizationMessageRepository.findByMessageKeyAndLanguage(CREATED_LOCALIZATION_KEY, "fr").orElseThrow();

        mockMvc.perform(put("/api/localizations/{id}", bookNotFoundEn.getId()).with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "messageKey": "audit.localization.updated",
              "language": "fr",
              "messageText": "Le livre demande est introuvable.",
              "description": "French message for missing book errors."
            }
            """)).andExpect(status().isOk());

        mockMvc.perform(delete("/api/localizations/{id}", bookNotFoundEn.getId()).with(adminSession.unsafeWrite())).andExpect(status().isNoContent());

        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByIdAsc();

        assertThat(auditLogs).hasSize(3);
        assertThat(auditLogs).extracting(AuditLog::getTargetType).containsExactly(
            AuditTargetType.LOCALIZATION_MESSAGE, AuditTargetType.LOCALIZATION_MESSAGE, AuditTargetType.LOCALIZATION_MESSAGE
        );
        assertThat(auditLogs).extracting(AuditLog::getAction).containsExactly(AuditAction.CREATE, AuditAction.UPDATE, AuditAction.DELETE);
        assertThat(auditLogs).extracting(AuditLog::getTargetId).containsExactly(createdMessage.getId(), bookNotFoundEn.getId(), bookNotFoundEn.getId());
        assertThat(auditLogs).extracting(AuditLog::getActorLogin).containsOnly("admin-user");
        assertThat(auditLogs).allSatisfy(auditLog -> {
            assertThat(auditLog.getActorUser()).isNotNull();
            assertThat(auditLog.getCreatedAt()).isNotNull();
            assertThat(auditLog.getSummary()).isNotBlank();
            assertThat(auditLog.getDetails()).isNotEmpty();
        });
    }

    @Test
    void categoryCreateUpdateAndDeleteProduceAuditLogsWithAdminActorAndStructuredDetails() throws Exception {
        BrowserSession adminSession = adminSession();

        mockMvc.perform(post("/api/categories").with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "name": "Architecture"
            }
            """)).andExpect(status().isCreated());

        Category createdCategory = categoryRepository.findAllByOrderByNameAsc().stream().filter(category -> "Architecture".equals(category.getName())).findFirst().orElseThrow();

        mockMvc.perform(put("/api/categories/{id}", createdCategory.getId()).with(adminSession.unsafeWrite()).contentType(MediaType.APPLICATION_JSON).content("""
            {
              "name": "Platform"
            }
            """)).andExpect(status().isOk());

        mockMvc.perform(delete("/api/categories/{id}", createdCategory.getId()).with(adminSession.unsafeWrite())).andExpect(status().isNoContent());

        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByIdAsc();

        assertThat(auditLogs).hasSize(3);
        assertThat(auditLogs).extracting(AuditLog::getTargetType).containsExactly(AuditTargetType.CATEGORY, AuditTargetType.CATEGORY, AuditTargetType.CATEGORY);
        assertThat(auditLogs).extracting(AuditLog::getAction).containsExactly(AuditAction.CREATE, AuditAction.UPDATE, AuditAction.DELETE);
        assertThat(auditLogs).extracting(AuditLog::getTargetId).containsExactly(createdCategory.getId(), createdCategory.getId(), createdCategory.getId());
        assertThat(auditLogs).extracting(AuditLog::getActorLogin).containsOnly("admin-user");
        assertThat(auditLogs.get(0).getDetails()).containsEntry("name", "Architecture");
        assertThat(auditLogs.get(1).getDetails()).containsEntry("previousName", "Architecture").containsEntry("name", "Platform");
        assertThat(auditLogs.get(2).getDetails()).containsEntry("name", "Platform");
    }

    @Test
    void logoutProducesAuthenticationAuditLog() throws Exception {
        BrowserSession readerSession = readerSession();

        mockMvc.perform(get("/api/account").with(readerSession.authenticatedSession())).andExpect(status().isOk());

        mockMvc.perform(post("/api/session/logout").with(readerSession.unsafeWrite())).andExpect(status().isNoContent());

        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByIdAsc();

        assertThat(auditLogs).hasSize(1);
        AuditLog logoutAuditLog = auditLogs.getFirst();
        assertThat(logoutAuditLog.getTargetType()).isEqualTo(AuditTargetType.AUTHENTICATION);
        assertThat(logoutAuditLog.getAction()).isEqualTo(AuditAction.LOGOUT);
        assertThat(logoutAuditLog.getActorLogin()).isEqualTo("reader-user");
        assertThat(logoutAuditLog.getSummary()).isEqualTo("Logged out current session for 'reader-user'.");
        assertThat(logoutAuditLog.getDetails()).containsEntry("provider", "github").containsEntry("login", "reader-user");
    }

    private BrowserSession adminSession() {
        return adminBrowserSession(sessionRepository);
    }

    private BrowserSession readerSession() {
        return authenticatedBrowserSession(sessionRepository, "reader-user");
    }
}
