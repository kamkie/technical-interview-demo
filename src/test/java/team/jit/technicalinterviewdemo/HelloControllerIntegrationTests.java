package team.jit.technicalinterviewdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import team.jit.technicalinterviewdemo.testing.AbstractMockMvcIntegrationTest;
import team.jit.technicalinterviewdemo.testing.MockMvcIntegrationSpringBootTest;

@MockMvcIntegrationSpringBootTest
class HelloControllerIntegrationTests extends AbstractMockMvcIntegrationTest {

    @Test
    void helloEndpointReturnsHelloWorld() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }
}
