package id.co.fadly;

import id.co.fadly.model.RequestOrderDto;
import id.co.fadly.model.ResponseOrderDto;
import id.co.fadly.service.ConsumerService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ConsumerServiceTest {

    @Inject
    ConsumerService consumerService;

    private RequestOrderDto order;

    @BeforeEach
    void setUp() {
        order = new RequestOrderDto();
        order.id = "TEST-SUCCESS-001";
    }

    @Test
    void testProcessHighAmount() {
        order.amount = 1500.0;

        ResponseOrderDto result = consumerService.process(order);

        Assertions.assertEquals("HIGH", result.orders.status);
        Assertions.assertEquals(1500.0, result.orders.amount);
    }

    @Test
    void testProcessNormalAmount() {
        order.amount = 500.0;

        ResponseOrderDto result = consumerService.process(order);

        Assertions.assertEquals("NORMAL", result.orders.status);
    }

    @Test
    void testProcessFailureCase() {
        order.id = "failed";
        order.amount = 1000.0;

        Assertions.assertThrows(RuntimeException.class, () -> {
            consumerService.process(order);
        });
    }


}
