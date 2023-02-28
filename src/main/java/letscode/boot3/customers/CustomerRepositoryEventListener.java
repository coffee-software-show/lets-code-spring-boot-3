package letscode.boot3.customers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

// only for the http save operation
@Slf4j
@Component
@RepositoryEventHandler
class CustomerRepositoryEventListener {

    @HandleBeforeSave
    public void before(Customer customer) {
        log.info("beforeCustomerSaved: {}", customer.toString());
    }

    @HandleAfterSave
    public void handleCustomerSave(Customer entity) {
        log.info("new customer saved: {}", entity.toString());
    }

}
