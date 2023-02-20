package letscode.boot3;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;

class DefaultCustomerServiceTest {

    @Test
    void all() throws Exception  {
        var ds = Mockito.mock(DataSource.class);
        var cs = new DefaultCustomerService(ds);

    }

}