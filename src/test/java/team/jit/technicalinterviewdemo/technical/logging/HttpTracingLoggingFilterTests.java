package team.jit.technicalinterviewdemo.technical.logging;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpTracingLoggingFilterTests {

    @Mock
    private Tracer tracer;

    private HttpTracingLoggingFilter filter;

    @BeforeEach
    void setUp() {
        filter = new HttpTracingLoggingFilter(tracer);
        when(tracer.currentSpan()).thenReturn(null);
    }

    @Test
    void unsafeRequestIdIsReplacedBeforeItReachesTheResponseHeader() throws Exception {
        String forgedRequestId = "request-123\r\nforged-entry";
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hello");
        request.addHeader(HttpTracingLoggingFilter.REQUEST_ID_HEADER, forgedRequestId);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, new MockFilterChain());

        assertThat(response.getHeader(HttpTracingLoggingFilter.REQUEST_ID_HEADER))
                .isNotBlank();
        assertThat(response.getHeader(HttpTracingLoggingFilter.REQUEST_ID_HEADER))
                .isNotEqualTo(forgedRequestId);
        assertThat(response.getHeader(HttpTracingLoggingFilter.REQUEST_ID_HEADER))
                .doesNotContain("\r")
                .doesNotContain("\n");
    }
}
