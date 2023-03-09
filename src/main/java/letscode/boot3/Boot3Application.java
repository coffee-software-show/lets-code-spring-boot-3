package letscode.boot3;


import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.http.HttpServletRequest;
import letscode.boot3.customers.Customer;
import letscode.boot3.customers.CustomerRepository;
import letscode.boot3.customers.CustomersNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.observability.ContextProviderFactory;
import org.springframework.data.mongodb.observability.MongoObservationCommandListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.filter.ServerHttpObservationFilter;

import java.util.Map;
import java.util.Set;

// todo: Spring Session


@Slf4j
@SpringBootApplication
public class Boot3Application {

    public static void main(String[] args) {
        SpringApplication.run(Boot3Application.class, args);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> valueLoggingListener(@Value("${test}") String test) {
        return event -> log.info("the value for test is coming from [{}]", test);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener(
            CustomerRepository repository) {
        return event -> {
            repository.deleteAll();
            Set.of("A", "B", "C", "D").forEach(c -> repository.save(new Customer(null, c, Math.random() > .5)));
            repository.findAll(letscode.boot3.customers.QCustomer.customer.name.startsWith("A").not()).forEach(System.out::println);
        };
    }

    @Bean
    ApplicationListener<WebServerInitializedEvent> webServerInitializedEventApplicationListener() {
        return event -> log.info("the web server is ready to serve HTTP traffic on port {}", event.getWebServer().getPort());
    }

    @Bean
    ApplicationListener<AvailabilityChangeEvent<? extends AvailabilityState>> availabilityChangeEventApplicationListener() {
        return event ->
                log.info("the service is healthy? {}", event.getState().toString());
    }
}

@Component
@Endpoint(id = "jonatan")
class IsJonatanAwesomeEndpoint {

    @ReadOperation
    Map<String, Object> awesome() {
        return Map.of("awesome", true);
    }

}


@Slf4j
@ControllerAdvice
class ObservabilityConfiguration {

    @ExceptionHandler
    ResponseEntity<?> customerNotFoundExceptionHandler(
            HttpServletRequest request, CustomersNotFoundException customerNotFoundException) {
        log.info("couldn't find the customer with name {}", customerNotFoundException.name());
        ServerHttpObservationFilter
                .findObservationContext(request)
                .ifPresent(context -> context.setError(customerNotFoundException));
        return ResponseEntity.notFound().build();

    }

    @Bean
    MongoClientSettingsBuilderCustomizer mongoMetricsSynchronousContextProvider(ObservationRegistry registry) {
        return (clientSettingsBuilder) -> clientSettingsBuilder
                .contextProvider(ContextProviderFactory.create(registry))
                .addCommandListener(new MongoObservationCommandListener(registry));
    }
}


@Slf4j
@Controller
@ResponseBody
class DownController {

    private final ApplicationEventPublisher publisher;

    DownController(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping("/slow")
    void slow() throws Exception {
        var seconds = 20;
        log.info("about to sleep " + seconds + "s.");
        Thread.sleep(seconds * 1000);
        log.info("slept " + seconds + "s.");
    }


    @GetMapping("/down")
    void down() {
        this.publisher.publishEvent(
                new AvailabilityChangeEvent<LivenessState>(this, LivenessState.BROKEN));
    }
}