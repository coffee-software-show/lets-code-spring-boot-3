package letscode.boot3.customers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
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

    @QueryMapping
    Iterable<Customer> customersByName(@Argument String name) {
        return this.customerRepository.findByName(name);
    }

    @QueryMapping
    Customer customerById(@Argument Integer id) {
        return this.customerRepository.findById(id).orElseGet(null);
    }

    @SubscriptionMapping
    Flux<Customer> newCustomers() {
        return Flux.fromIterable( this.customers()).delayElements(Duration.ofSeconds(1));
    }

    @BatchMapping
    Map<Customer, Profile> profile(List<Customer> customers) {
        log.info("calling the batch profile endpoint");
        var map = new HashMap<Customer, Profile>();
        for (var c : customers)
            map.put(c, new Profile(c.getId())); // todo call batch network service
        return map;
    }

    /*@SchemaMapping(typeName = "Customer")
    Mono<Profile> profile(Customer customer) throws Exception {
        log.info("calling profile microservice for customer #{}", customer.getId());
        return Mono.just(new Profile(customer.getId())).delayElement(Duration.ofSeconds(1));
    }
*/
    @QueryMapping
    Iterable<Customer> customers() {
        return this.customerRepository.findAll();
    }
}

record Profile(Integer id) {
}