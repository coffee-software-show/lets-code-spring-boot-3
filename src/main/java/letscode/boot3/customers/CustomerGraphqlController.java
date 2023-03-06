package letscode.boot3.customers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
class CustomerGraphqlController {

    private final CustomerRepository customerRepository;

    CustomerGraphqlController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @MutationMapping
    Customer createCustomer(@Argument String name, @Argument boolean subscribed) {
        return this.customerRepository.save(new Customer(null, name, subscribed));
    }

    @SubscriptionMapping
    Flux<Customer> newCustomers() {
        return Flux.fromIterable(this.customers()).delayElements(Duration.ofSeconds(1));
    }

    @BatchMapping
    Map<Customer, Profile> profile(List<Customer> customers) {
        log.info("calling the batch profile endpoint");
        var map = new HashMap<Customer, Profile>();
        for (var c : customers)
            map.put(c, new Profile(c.id())); // todo call batch network service
        return map;
    }

    @QueryMapping
    Iterable<Customer> customers() {
        log.info("returning all the customers.");
        return this.customerRepository.findAll();
    }
}

@Controller
@ResponseBody
@Slf4j
class HelloWorldController {

    @GetMapping("/hello/{name}")
    Map<String, String> hello(@PathVariable String name) {
        log.info("hello, " + name + '!');
        return Map.of("message", "Hello, " + name + "!");
    }
}

record Profile(String id) {
}