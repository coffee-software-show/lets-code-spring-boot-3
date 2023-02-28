package letscode.boot3.customers;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table (name = "customers" )
public record Customer(@Id Integer id,  String name, boolean subscribed) {
}
