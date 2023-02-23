package letscode.boot3;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class Boot3Application {

    public static void main(String[] args) {
        SpringApplication.run(Boot3Application.class, args);
    }


    @Bean
    ApplicationListener<WebServerInitializedEvent> webServerInitializedEventApplicationListener() {
        return event -> log.info("the web server is ready to serve HTTP traffic on port {}",
                event.getWebServer().getPort());
    }

    @Bean
    ApplicationListener<AvailabilityChangeEvent<?>> availabilityChangeEventApplicationListener() {
        return event -> log.info("the service is healthy? {}", event.getState().toString());
    }

}

