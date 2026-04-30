package team.jit.technicalinterviewdemo.technical.testing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;
import team.jit.technicalinterviewdemo.TestcontainersTest;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestcontainersTest
@SpringBootTest
public @interface IntegrationSpringBootTest {
}
