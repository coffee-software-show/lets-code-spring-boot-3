package letscode.boot3.customers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Collection;
import java.util.List;

// rip records
@Data
@AllArgsConstructor
@Table(name = "customers")
public class Customer {

    @Id
    private Integer id;
    private final String name;
    private final boolean subscribed;

    @DomainEvents
    Collection<CustomerCreatedEvent> domainEvents() {
        return List.of(new CustomerCreatedEvent(this));
    }
}
