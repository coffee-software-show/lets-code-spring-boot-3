package letscode.boot3.customers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Controller
@ResponseBody
class CustomerHttpController {


    private final CustomerService customerService;

    CustomerHttpController(CustomerService customerService) {
        this.customerService = customerService;
    }


    // GET
    // POST
    // DELETE
    // OPTIONS
    // PUT
    // PATCH
    //
/*@RequestMapping("/greeting")
	public HttpEntity<Greeting> greeting(
		@RequestParam(value = "name", defaultValue = "World") String name) {

		Greeting greeting = new Greeting(String.format(TEMPLATE, name));
		greeting.add(linkTo(methodOn(GreetingController.class).greeting(name)).withSelfRel());

		return new ResponseEntity<>(greeting, HttpStatus.OK);
	}*/


    @Data
    @EqualsAndHashCode(callSuper = true)
    @RequiredArgsConstructor
    static class CustomerModel extends RepresentationModel<CustomerModel> {
        private final Customer customer;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @RequiredArgsConstructor
    static class CustomersModel extends RepresentationModel<CustomersModel> {
        private final Collection<Customer> customers;
    }

    @GetMapping("/customers")
    HttpEntity<CustomersModel> customers() {
        var customers = this.customerService.all();
        var customerRepresentationalModel = new CustomersModel(customers);
        customerRepresentationalModel.add(linkTo(methodOn(CustomerHttpController.class).customers()).withSelfRel());
        customerRepresentationalModel.add(linkTo(methodOn(CustomerHttpController.class).customersById(null)).withRel("customers-by-id"));
        return ResponseEntity.ok(customerRepresentationalModel);
    }

    @GetMapping("/customers/{id}")
    HttpEntity<CustomerModel> customersById(@PathVariable Integer id) {
        var customer = this.customerService.byId(id);
        var customerRepresentationalModel = new CustomerModel(customer);
        customerRepresentationalModel.add(linkTo(methodOn(CustomerHttpController.class).customers()).withRel("customers"));
        customerRepresentationalModel.add(linkTo(methodOn(CustomerHttpController.class).customersById(id)).withSelfRel());
        return ResponseEntity.ok(customerRepresentationalModel);
    }


}