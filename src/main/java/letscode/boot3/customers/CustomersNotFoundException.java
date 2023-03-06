package letscode.boot3.customers;

// ow
public class CustomersNotFoundException extends IllegalArgumentException {

    private final String name;

    CustomersNotFoundException(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }
}
