package team.jit.technicalinterviewdemo.testing;

import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MockMvcIntegrationSpringBootTest
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public @interface RestDocsIntegrationSpringBootTest {
}
