package team.jit.technicalinterviewdemo.technical.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

public final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    private final CsrfTokenRequestHandler plainRequestHandler = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xorRequestHandler = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(
                       HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> deferredCsrfToken
    ) {
        xorRequestHandler.handle(request, response, deferredCsrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        String headerValue = request.getHeader(csrfToken.getHeaderName());
        if (StringUtils.hasText(headerValue)) {
            return plainRequestHandler.resolveCsrfTokenValue(request, csrfToken);
        }
        return xorRequestHandler.resolveCsrfTokenValue(request, csrfToken);
    }
}
