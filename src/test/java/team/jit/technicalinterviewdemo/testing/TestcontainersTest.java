package team.jit.technicalinterviewdemo.testing;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ActiveProfiles("test")
@Import(PostgresTestcontainersConfiguration.class)
public @interface TestcontainersTest {
}
