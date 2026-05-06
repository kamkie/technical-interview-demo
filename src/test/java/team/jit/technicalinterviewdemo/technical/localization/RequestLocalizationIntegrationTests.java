package team.jit.technicalinterviewdemo.technical.localization;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.testing.AbstractBookCatalogMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcIntegrationSpringBootTest
class RequestLocalizationIntegrationTests extends AbstractBookCatalogMockMvcIntegrationTest {

    @Test
    void acceptLanguageHeaderReturnsLocalizedErrorMessage() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 9999).header("Accept-Language", "es-ES,es;q=0.9,en;q=0.8")).andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Book Not Found")).andExpect(jsonPath("$.detail").value("Book with id 9999 was not found.")).andExpect(jsonPath("$.messageKey").value("error.book.not_found")).andExpect(jsonPath("$.message").value("No se encontro el libro solicitado.")).andExpect(jsonPath("$.language").value("es"));
    }

    @Test
    void langQueryParameterOverridesAcceptLanguageHeader() throws Exception {
        mockMvc.perform(get("/api/books").queryParam("year", "2018").queryParam("yearFrom", "2000").queryParam("lang", "uk-UA").header("Accept-Language", "es-ES,es;q=0.9")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("Invalid Request")).andExpect(jsonPath("$.detail").value(
            "Use either 'year' or the 'yearFrom'/'yearTo' range parameters, not both."
        )).andExpect(jsonPath("$.messageKey").value("error.request.invalid")).andExpect(jsonPath("$.message").value("Zapyt ye nevalidnym.")).andExpect(jsonPath("$.language").value("uk"));
    }

    @Test
    void invalidLangQueryParameterFallsBackToEnglish() throws Exception {
        mockMvc.perform(get("/api/books/{id}", "abc").queryParam("lang", "zz-ZZ").header("Accept-Language", "pl-PL,pl;q=0.9")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("Invalid Parameter")).andExpect(jsonPath("$.detail").value("Parameter 'id' value 'abc' is invalid.")).andExpect(jsonPath("$.messageKey").value("error.request.invalid_parameter")).andExpect(jsonPath("$.message").value("A request parameter has an invalid value.")).andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void languageCookieIsUsedWhenAcceptLanguageIsUnsupportedAndQueryOverrideIsAbsent() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 9999).header("Accept-Language", "it-IT,it;q=0.9").cookie(new Cookie("language", "pl-PL"))).andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Book Not Found")).andExpect(jsonPath("$.detail").value("Book with id 9999 was not found.")).andExpect(jsonPath("$.messageKey").value("error.book.not_found")).andExpect(jsonPath("$.message").value("Nie znaleziono zadanej ksiazki.")).andExpect(jsonPath("$.language").value("pl"));
    }

    @Test
    void unsupportedAcceptLanguageFallsBackToLanguageCookie() throws Exception {
        mockMvc.perform(get("/api/books").queryParam("year", "2018").queryParam("yearFrom", "2000").header("Accept-Language", "it-IT,it;q=0.9").cookie(new Cookie("language", "no"))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("Invalid Request")).andExpect(jsonPath("$.detail").value(
            "Use either 'year' or the 'yearFrom'/'yearTo' range parameters, not both."
        )).andExpect(jsonPath("$.messageKey").value("error.request.invalid")).andExpect(jsonPath("$.message").value("Foresporselen er ugyldig.")).andExpect(jsonPath("$.language").value("no"));
    }
}
