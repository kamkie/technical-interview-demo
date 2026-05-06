package team.jit.technicalinterviewdemo.business.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserAccountLanguageRequest", description = "Payload for updating the current user's preferred language.")
public record UserAccountLanguageRequest(
                                         @Schema(
                                                 description = "Two-letter ISO 639-1 language code. Use null or blank to clear the preference.", example = "pl"
                                         ) String preferredLanguage
) {
}
