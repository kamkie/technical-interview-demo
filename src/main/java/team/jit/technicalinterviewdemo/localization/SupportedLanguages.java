package team.jit.technicalinterviewdemo.localization;

import java.util.List;
import java.util.Set;

final class SupportedLanguages {

    private static final List<String> VALUES = LocalizationMessageSeedData.supportedLanguages();
    private static final Set<String> VALUE_SET = Set.copyOf(VALUES);

    private SupportedLanguages() {
    }

    static boolean isSupported(String language) {
        return VALUE_SET.contains(language);
    }

    static String description() {
        return String.join(", ", VALUES);
    }
}
