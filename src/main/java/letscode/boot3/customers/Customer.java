package letscode.boot3.customers;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@QueryEntity
@Document
public record Customer(@Id String id, String name, boolean subscribed) {
}