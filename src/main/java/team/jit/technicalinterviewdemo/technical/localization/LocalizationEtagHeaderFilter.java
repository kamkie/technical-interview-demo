package team.jit.technicalinterviewdemo.technical.localization;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Component
public class LocalizationEtagHeaderFilter extends ShallowEtagHeaderFilter {

    private static final String LOCALIZATIONS_PATH = "/api/localizations";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        return !path.equals(LOCALIZATIONS_PATH) && !path.startsWith(LOCALIZATIONS_PATH + "/");
    }
}
