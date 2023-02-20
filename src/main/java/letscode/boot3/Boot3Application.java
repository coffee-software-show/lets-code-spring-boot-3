package letscode.boot3;


import lombok.extern.slf4j.Slf4j;
import org.postgresql.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class Boot3Application {

    public static void main(String[] args) {
        var dataSource = new DriverManagerDataSource(
                "jdbc:postgresql://localhost/postgres",
                "postgres", "postgres");
        dataSource.setDriverClassName(Driver.class.getName());

        var template = new JdbcTemplate(dataSource);
        template.afterPropertiesSet();

        var ptm = new DataSourceTransactionManager(dataSource);
        ptm.afterPropertiesSet();

        var tt = new TransactionTemplate(ptm);
        tt.afterPropertiesSet();

        var cs = new TransactionalCustomerService(template, tt);

        var juergen = cs.add("Jürgen");
        var stephane = cs.add("Stéphane");
        var josh = cs.add("Josh");

        var all = cs.all();
        Assert.state(all.contains(juergen) && all.contains(stephane),
                "we didn't add Stéphane and Jürgen successfully!");
        all.forEach(c -> log.info(c.toString()));
    }

}

class TransactionalCustomerService extends CustomerService {

    private final TransactionTemplate tt;

    @Override
    Customer add(String name) {
        return this.tt.execute(status -> super.add(name));
    }

    @Override
    Customer byId(Integer id) {
        return this.tt.execute(status -> super.byId(id));
    }

    @Override
    Collection<Customer> all() {
        return this.tt.execute(t -> super.all());
    }

    TransactionalCustomerService(JdbcTemplate template, TransactionTemplate tt) {
        super(template);
        this.tt = tt;
    }
}

@Slf4j
class CustomerService {

    private final JdbcTemplate template;
    private final RowMapper<Customer> customerRowMapper =
            (resultSet, rowNum) -> new Customer(resultSet.getInt("id"), resultSet.getString("name"));

    CustomerService(JdbcTemplate template) {
        this.template = template;

    }

    Customer add(String name) {
        var al = new ArrayList<Map<String, Object>>();
        al.add(Map.of("id", Long.class));
        var keyHolder = new GeneratedKeyHolder(al);
        template.update(con -> {
            var ps = con.prepareStatement("""
                            insert into customers (name) values(?)
                            on conflict on constraint customers_name_key do update set name = excluded.name  
                            """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        var generatedId = keyHolder.getKeys().get("id");
        log.info("generatedId: {}", generatedId.toString());
        Assert.state(generatedId instanceof Number, "the generatedId must be a Number!");
        Number number = (Number) generatedId;
        return byId(number.intValue());


    }

    Customer byId(Integer id) {
        return template.queryForObject(
                "select id, name  from customers where id =? ", customerRowMapper, id);
    }

    Collection<Customer> all() {
        return template.query("select id, name  from customers", this.customerRowMapper);
    }

}

record Customer(Integer id, String name) {
}
