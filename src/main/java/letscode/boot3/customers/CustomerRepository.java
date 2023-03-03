package letscode.boot3.customers;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.Collection;

@GraphQlRepository
public interface CustomerRepository extends CrudRepository<Customer, Integer>  ,
        QuerydslPredicateExecutor <Customer> {
    Collection<Customer> findByName(String name);
}
