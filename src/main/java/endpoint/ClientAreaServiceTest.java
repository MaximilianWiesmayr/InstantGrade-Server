package endpoint;

import entity.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import util.Fakes;

import static org.junit.jupiter.api.Assertions.*;

class ClientAreaServiceTest {

    @Test
    @Order(0)
    public void testOverview() {
        User fakeVerifiedUser = Fakes.getFakeVerifiedUser();

    }

}