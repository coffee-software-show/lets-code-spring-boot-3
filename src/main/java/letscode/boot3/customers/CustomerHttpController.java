package letscode.boot3.customers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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



    @GetMapping("/customers/{id}")
    HttpEntity<CustomerRepresentationalModel> customerById(@PathVariable Integer id) {
        var customer = this.customerService.byId(id);
        var customerRepresentationalModel = new CustomerRepresentationalModel(
                customer.name(), customer.id());
        var link = linkTo(methodOn(CustomerHttpController.class).customers()).withRel("customers");
        customerRepresentationalModel.add(link);
        customerRepresentationalModel.add(
                linkTo(methodOn(CustomerHttpController.class).customerById(id)).withSelfRel()
        );
        return new ResponseEntity<>(customerRepresentationalModel, HttpStatus.OK);
    }

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.customerService.all();
    }


}