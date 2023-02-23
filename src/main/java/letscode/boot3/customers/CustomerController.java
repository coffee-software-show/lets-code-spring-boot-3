package letscode.boot3.customers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Controller
@ExposesResourceFor(Customer.class)
@ResponseBody
@RequestMapping("/customers")
class CustomerController {

    private final CustomerService customerService;

    private final CustomerModelAssembler customerModelAssembler;

    CustomerController(CustomerService customerService, CustomerModelAssembler customerModelAssembler) {
        this.customerService = customerService;
        this.customerModelAssembler = customerModelAssembler;
    }

    @GetMapping
    CollectionModel<CustomerModel> customers() {
        var customers = this.customerService.all();
        return this.customerModelAssembler.toCollectionModel(customers);
    }

    @GetMapping("{id}")
    CustomerModel customersById(@PathVariable Integer id) {
        return this.customerModelAssembler.toModel(this.customerService.byId(id));
    }
}


@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
class CustomerModel extends RepresentationModel<CustomerModel> {
    private final Customer customer;
}

@Component
class CustomerModelAssembler extends RepresentationModelAssemblerSupport<Customer, CustomerModel> {

    public CustomerModelAssembler() {
        super(CustomerController.class, CustomerModel.class);
    }

    @Override
    public CollectionModel<CustomerModel> toCollectionModel(Iterable<? extends Customer> entities) {
        var cm = super.toCollectionModel(entities);
        cm.add(linkTo(methodOn(CustomerController.class).customers()).withRel("customers"));
        return cm;
    }

    @Override
    public CustomerModel toModel(Customer person) {
        var resource = new CustomerModel(person);
        resource.add(linkTo(methodOn(CustomerController.class).customers()).withRel("customers"));
        resource.add(linkTo(methodOn(CustomerController.class).customersById(person.id())).withSelfRel());
        return resource;
    }
}


@Configuration
class HateosConfiguration {
/*
    @Bean
    CustomerRepresentationModelAssemblerSupport customerRepresentationModelAssemblerSupport() {
        return new CustomerRepresentationModelAssemblerSupport(
                CustomerHttpController.class,
                CustomerModel.class
        );
    }*/

}

/*
class CustomerRepresentationModelAssemblerSupport extends
        RepresentationModelAssemblerSupport<Customer, CustomerModel> {

    CustomerRepresentationModelAssemblerSupport(
            Class<?> controllerClass,
            Class<CustomerModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public CustomerModel toModel(Customer entity) {
        return new CustomerModel(entity);
    }
}
*/

/*

@Component
class CustomerModelProcessor implements RepresentationModelProcessor<Customer> {

    public static final String UNSUBSCRIBE_REL = "unsubscribe";

    private final @NonNull EntityLinks entityLinks;

    CustomerModelProcessor(@NonNull EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public EntityModel<CustomerModel> process(CustomerModel resource) {
        var typedEntityLinks = entityLinks.forType(Customer::id);
        var customer = resource.getCustomer();
        var customerLinks = typedEntityLinks.linkForItemResource(customer);
        return resource
                .addIf(!customer.subscribed(), () -> customerLinks.withRel(UNSUBSCRIBE_REL));
    }
}
*/
