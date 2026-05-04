package team.jit.technicalinterviewdemo.technical.security;

public final class SameSiteCsrfContract {

    public static final String COOKIE_NAME = "XSRF-TOKEN";
    public static final String HEADER_NAME = "X-XSRF-TOKEN";

    private SameSiteCsrfContract() {
    }
}
