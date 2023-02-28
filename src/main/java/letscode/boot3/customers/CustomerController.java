package letscode.boot3.customers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("{id}/subscription")
    ResponseEntity<?> deleteSubscription(@PathVariable Integer id ) {
        return ResponseEntity.ok().build();
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
        resource.addIf(person.subscribed(), () -> linkTo(methodOn(CustomerController.class)
                .deleteSubscription(person.id()))
                .withRel("cancelSubscription"));
        resource.add(linkTo(methodOn(CustomerController.class).customers()).withRel("customers"));
        resource.add(linkTo(methodOn(CustomerController.class).customersById(person.id())).withSelfRel());
        return resource;
    }
}