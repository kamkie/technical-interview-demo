package team.jit.technicalinterviewdemo.technical.docs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class DocumentationWebConfiguration implements WebMvcConfigurer {

    private static final String CLASSPATH_DOCS_LOCATION = "classpath:/static/docs/";
    private static final String GENERATED_DOCS_LOCATION = "file:build/docs/asciidoc/";

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/docs").setViewName("redirect:/docs/index.html");
        registry.addViewController("/docs/").setViewName("redirect:/docs/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/docs/**").addResourceLocations(CLASSPATH_DOCS_LOCATION, GENERATED_DOCS_LOCATION);
    }
}
