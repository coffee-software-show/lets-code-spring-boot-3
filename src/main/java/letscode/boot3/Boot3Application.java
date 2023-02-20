package letscode.boot3;


import lombok.extern.slf4j.Slf4j;
import org.postgresql.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class Boot3Application {

    public static void main(String[] args) {
        var dataSource = new DriverManagerDataSource(
                "jdbc:postgresql://localhost/postgres",
                "postgres", "postgres");
        dataSource.setDriverClassName(Driver.class.getName());

        var template = new JdbcTemplate(dataSource);
        template.afterPropertiesSet();

        var cs = new DefaultCustomerService(template);
        var all = cs.all();
        all.forEach(c -> log.info(c.toString()));
    }

}

@Slf4j
class DefaultCustomerService {

    private final JdbcTemplate template;

    DefaultCustomerService(JdbcTemplate template) {
        this.template = template;
    }

    Collection<Customer> all() {
        var listOfCustomers = new ArrayList<Customer>();
        try {
            try (var connection = this.dataSource.getConnection()) {
                try (var stmt = connection.createStatement()) {
                    try (var resultSet = stmt.executeQuery("select * from customers")) {
                        while (resultSet.next()) {
                            listOfCustomers.add(new Customer(resultSet.getInt("id"), resultSet.getString("name")));
                        }
                    }
                }
            }
        } //
        catch (Exception th) {
            log.error("something went terribly wrong, but search me, I have no idea what!",
                    th);
        }
        return listOfCustomers;
    }

}

record Customer(Integer id, String name) {
}

