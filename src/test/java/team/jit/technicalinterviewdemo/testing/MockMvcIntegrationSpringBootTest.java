package team.jit.technicalinterviewdemo.testing;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@IntegrationSpringBootTest
@AutoConfigureMockMvc
public @interface MockMvcIntegrationSpringBootTest {
}
