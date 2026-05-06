package team.jit.technicalinterviewdemo.technical.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiProblemResponse", description = "Localized ProblemDetail payload returned for API errors.")
public record ApiProblemResponse(
    @Schema(description = "Problem title.", example = "Unauthorized") String title,
    @Schema(description = "HTTP status code.", example = "401") Integer status,
    @Schema(description = "Technical problem detail kept stable for debugging and logs.", example = "Authentication is required to access this resource.") String detail,
    @Schema(description = "Stable localization key for the error type.", example = "error.request.unauthorized") String messageKey,
    @Schema(description = "Localized end-user message resolved from the request language.", example = "You must authenticate before performing this operation.") String message,
    @Schema(description = "Two-letter ISO 639-1 language code actually used for the localized message.", example = "en") String language
) {
}
