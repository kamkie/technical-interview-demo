package team.jit.technicalinterviewdemo.business.localization;

import java.util.List;
import java.util.Set;
import team.jit.technicalinterviewdemo.business.localization.seed.LocalizationMessageSeedData;

public final class SupportedLanguages {

    private static final List<String> VALUES = LocalizationMessageSeedData.supportedLanguages();
    private static final Set<String> VALUE_SET = Set.copyOf(VALUES);

    private SupportedLanguages() {
    }

    public static boolean isSupported(String language) {
        return VALUE_SET.contains(language);
    }

    public static String description() {
        return String.join(", ", VALUES);
    }
}

