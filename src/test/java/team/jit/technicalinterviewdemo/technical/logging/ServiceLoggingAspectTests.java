package team.jit.technicalinterviewdemo.technical.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class ServiceLoggingAspectTests {

    @Test
    void aspectLogsServiceCallsButLeavesNonServiceBeansUntouched(CapturedOutput output) {
        LoggedService service = createProxy(new LoggedService());
        PlainComponent component = createProxy(new PlainComponent());

        assertThat(service.perform()).isEqualTo("service-result");
        assertThat(component.perform()).isEqualTo("component-result");

        String serviceLogLine = findLogLine(output, "Service call LoggedService.perform parameters={} durationMs=");
        assertThat(serviceLogLine).contains("Service call LoggedService.perform parameters={} durationMs=");
        assertThat(output.getOut()).doesNotContain("PlainComponent.perform");
    }

    private <T> T createProxy(T target) {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(target);
        proxyFactory.addAspect(new ServiceLoggingAspect());
        @SuppressWarnings("unchecked") T proxy = (T) proxyFactory.getProxy();
        return proxy;
    }

    private String findLogLine(CapturedOutput output, String messageFragment) {
        return Arrays.stream(output.getOut().split("\\R")).filter(line -> line.contains(messageFragment)).findFirst().orElseThrow(() -> new AssertionError("Missing log entry fragment: " + messageFragment));
    }

    @Service
    static class LoggedService {

        String perform() {
            return "service-result";
        }
    }

    @Component
    static class PlainComponent {

        String perform() {
            return "component-result";
        }
    }
}
