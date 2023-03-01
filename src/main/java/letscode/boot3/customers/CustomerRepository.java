package letscode.boot3.customers;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
    Collection<Customer> findByName(String name);
}
