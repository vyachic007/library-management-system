package by.slava_borisov.library.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan({
        "by.slava_borisov.library.controller",
        "by.slava_borisov.library.exception"
})
public class WebConfig {
}