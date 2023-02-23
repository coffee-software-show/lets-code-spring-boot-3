package letscode.boot3.customers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Slf4j
@Configuration
class CustomerInitializationConfiguration {

    @Bean
    ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(CustomerService cs) {
        return event -> {
            log.info("cs.class={}", cs.getClass().getName());
            var juergen = cs.add("Jürgen" ,false);
            var stephane = cs.add("Stéphane" ,false);
            var josh = cs.add("Josh" ,true );
            var all = cs.all();
            Assert.state(all.contains(juergen) && all.contains(stephane),
                    "we didn't add Stéphane and Jürgen successfully!");
            all.forEach(c -> log.info(c.toString()));
        };
    }
}
