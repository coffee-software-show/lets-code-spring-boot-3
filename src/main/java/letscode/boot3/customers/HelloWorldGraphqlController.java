package letscode.boot3.customers;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
class HelloWorldGraphqlController {

    @SchemaMapping(typeName = "Query", field = "hello")
    String hello(@Argument String name) {
        return "Hello, " + name + "!";
    }
}
