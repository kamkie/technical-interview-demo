package team.jit.technicalinterviewdemo.testing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MockMvcIntegrationSpringBootTest
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public @interface RestDocsIntegrationSpringBootTest {}
