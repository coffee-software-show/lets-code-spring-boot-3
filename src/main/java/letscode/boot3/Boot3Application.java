package letscode.boot3;


import letscode.boot3.customers.Customer;
import letscode.boot3.customers.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import java.util.Set;

@Slf4j
@SpringBootApplication
public class Boot3Application {

    public static void main(String[] args) {
        SpringApplication.run(Boot3Application.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener(
            CustomerRepository repository) {
        return event -> {
            repository.deleteAll();
            Set.of("A", "B", "C", "D").forEach(c -> repository.save(new Customer(null,
                    c, Math.random() > .5)));
            repository.findAll(letscode.boot3.customers.QCustomer.customer.name.startsWith("A").not())
                  .forEach(System.out::println);
        };
    }

    @Bean
    ApplicationListener<WebServerInitializedEvent> webServerInitializedEventApplicationListener() {
        return event -> log.info("the web server is ready to serve HTTP traffic on port {}", event.getWebServer().getPort());
    }

    @Bean
    ApplicationListener<AvailabilityChangeEvent<?>> availabilityChangeEventApplicationListener() {
        return event -> log.info("the service is healthy? {}", event.getState().toString());
    }
}
