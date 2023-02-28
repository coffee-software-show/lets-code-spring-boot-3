package letscode.boot3.customers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@Transactional
class CustomerService {

    private final ApplicationEventPublisher publisher;
    private final CustomerRepository repository;

    CustomerService(ApplicationEventPublisher publisher, CustomerRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    public Customer add(String name, boolean subscribed) {
        var saved = this.repository.save(new Customer(null, name, subscribed));
        this.publisher.publishEvent(new CustomerCreatedEvent(saved));
        return saved;
    }

    public Customer byId(Integer id) {
        return this.repository.findById(id).orElse(null);
    }

    public Collection<Customer> all() {
        var iterable = repository.findAll();
        var list = new ArrayList<Customer>();
        iterable.forEach(list::add);
        return list;
    }
}
