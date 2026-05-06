package team.jit.technicalinterviewdemo.business.audit;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "AuditLogPageResponse", description = "Paginated audit log response.")
public record AuditLogPageResponse(
                                   @ArraySchema(schema = @Schema(implementation = AuditLogResponse.class)) List<AuditLogResponse> content,
                                   @Schema(description = "Pagination request metadata.") Object pageable,
                                   @Schema(description = "Applied sort metadata.") Object sort,
                                   @Schema(description = "Total number of pages.", example = "1") int totalPages,
                                   @Schema(description = "Total number of matching audit log entries.", example = "3") long totalElements,
                                   @Schema(description = "Whether this page is the last page.", example = "true") boolean last,
                                   @Schema(description = "Requested page size.", example = "20") int size,
                                   @Schema(description = "Current zero-based page index.", example = "0") int number,
                                   @Schema(description = "Number of audit entries returned in the current page.", example = "3") int numberOfElements,
                                   @Schema(description = "Whether this page is the first page.", example = "true") boolean first,
                                   @Schema(description = "Whether the page content is empty.", example = "false") boolean empty
) {
}
