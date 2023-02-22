package letscode.boot3.customers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

class CustomerRepresentationalModel
        extends RepresentationModel<CustomerRepresentationalModel> {

    private final String name;
    private final Integer id;

    @JsonCreator
    CustomerRepresentationalModel(
            @JsonProperty("name") String name, @JsonProperty("id") Integer id) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }
}
