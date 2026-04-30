package team.jit.technicalinterviewdemo.testing;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Snippet;

public abstract class AbstractDocumentationIntegrationTest extends AbstractMockMvcIntegrationTest {

    protected RestDocumentationResultHandler documentEndpoint(String identifier, Snippet... snippets) {
        return document(
                identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                snippets
        );
    }

    protected HeaderDescriptor[] commonResponseHeaders() {
        return new HeaderDescriptor[]{
                headerWithName("X-Request-Id").description("Request identifier returned on every public endpoint."),
                headerWithName("traceparent").description("Trace context header returned when tracing is active.")
        };
    }

    protected FieldDescriptor[] problemResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("title").description("Problem title."),
                fieldWithPath("status").description("HTTP status code."),
                fieldWithPath("detail").description("Technical problem detail kept stable for debugging and logs."),
                fieldWithPath("messageKey").description("Stable localization key for the error type."),
                fieldWithPath("message").description("Localized end-user message resolved from the request language."),
                fieldWithPath("language").description("Two-letter ISO 639-1 language code actually used for the localized message.")
        };
    }

    protected FieldDescriptor[] problemResponseFieldsWithFieldErrors() {
        return new FieldDescriptor[]{
                fieldWithPath("title").description("Problem title."),
                fieldWithPath("status").description("HTTP status code."),
                fieldWithPath("detail").description("Technical problem detail kept stable for debugging and logs."),
                fieldWithPath("messageKey").description("Stable localization key for the error type."),
                fieldWithPath("message").description("Localized end-user message resolved from the request language."),
                fieldWithPath("language").description("Two-letter ISO 639-1 language code actually used for the localized message."),
                subsectionWithPath("fieldErrors").description("Validation errors keyed by request field name.")
        };
    }
}
