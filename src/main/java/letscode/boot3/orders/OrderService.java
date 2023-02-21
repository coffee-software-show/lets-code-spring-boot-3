package letscode.boot3.orders;

import letscode.boot3.customers.CustomerCreatedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
class OrderService implements ApplicationListener<CustomerCreatedEvent> {

    @Override
    public void onApplicationEvent(CustomerCreatedEvent event) {

    }
}
