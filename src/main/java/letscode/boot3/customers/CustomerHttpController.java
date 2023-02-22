package letscode.boot3.customers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.lang.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Slf4j
@Controller
class CustomerHttpController implements ApplicationListener<CustomerCreatedEvent> {

    private final Set<Customer> customersCache =
            new ConcurrentSkipListSet<>(Comparator.comparing(Customer::id));

    @ResponseBody
    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.customersCache;
    }

    private static class HardcodedCustomersView implements View {

        @Override
        public String getContentType() {
            return MediaType.TEXT_HTML_VALUE;
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            var customers = model.get("customers");
            if (customers instanceof Set<?> list) {
                var listOfCustomers = new ArrayList<Customer>();
                for (var c : list) {
                    if (c instanceof Customer customer)
                        listOfCustomers.add(customer);
                }
                var string = listOfCustomers.stream().map(c -> String.format("""
                                <tr>
                                <td> 
                                %s 
                                 </td>
                                <td>
                                %s
                                 </td>
                                </tr>
                                """, c.id(), c.name()))
                        .collect(Collectors.joining());
                try (var printWriter = response.getWriter()) {
                    printWriter.println(String.format("""
                            <h1> Customers with customer view </h1> 
                            <table>
                               %s
                             </table>
                                             
                                    """, string));
                    printWriter.flush();
                }
            }
        }
    }

    @GetMapping("/customers.do")
    String renderCustomerById(@RequestParam Integer id, Model model) {
        log.info("the customer ID is " + id);
        model.addAttribute("customer", this.customersCache.stream()
                .filter(f -> f.id().equals(id))
                .collect(Collectors.toSet()).iterator().next()) ;
        return "customer";
    }

    @GetMapping("/customers.custom")
    ModelAndView renderCustomersWithCustomView() {
        return new ModelAndView(new HardcodedCustomersView(), Map.of("customers", this.customersCache));
    }

    @GetMapping("/customers.mustache")
    ModelAndView renderCustomersWithMustache() {
        return new ModelAndView("customers-mu", Map.of("customers", this.customersCache));
    }

    @GetMapping("/customers.thymeleaf")
    ModelAndView renderCustomersWithThymeleaf() {
        return new ModelAndView("customers-tl", Map.of("customers", this.customersCache));
    }

    @Override
    public void onApplicationEvent(CustomerCreatedEvent event) {
        this.customersCache.add(event.getSource());
    }
}


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@org.springframework.stereotype.Controller
@interface RSocketController {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @return the suggested component name, if any (or empty String otherwise)
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

}
