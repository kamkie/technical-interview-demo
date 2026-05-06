package team.jit.technicalinterviewdemo.testing;

import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestcontainersTest
@SpringBootTest
public @interface IntegrationSpringBootTest {
}
