package letscode.boot3.customers;

import org.springframework.context.ApplicationEvent;

/**
 * broadscasts availability of a new {@code Customer} in the SQL DB.
 */
public class CustomerCreatedEvent extends ApplicationEvent {

    public CustomerCreatedEvent(Customer source) {
        super(source);
    }

    @Override
    public Customer getSource() {
        return (Customer) super.getSource();
    }
}
