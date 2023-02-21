package letscode.boot3.customers;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Controller
@ResponseBody
class CustomerHttpController implements ApplicationListener<CustomerCreatedEvent> {

    private final Set<Customer> customersCache =
            new ConcurrentSkipListSet<>(Comparator.comparing(Customer::id));

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.customersCache;
    }

    @Override
    public void onApplicationEvent(CustomerCreatedEvent event) {
        this.customersCache.add(event.getSource());
    }
}
