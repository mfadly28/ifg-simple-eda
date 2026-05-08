package id.co.fadly.service;

import id.co.fadly.model.RequestOrderDto;
import id.co.fadly.model.ResponseOrderDto;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class ConsumerService {
    @Incoming("orders-in")
    @Outgoing("orders-processed")
    @Retry(maxRetries = 3, delay = 1000)
    public ResponseOrderDto process(RequestOrderDto request) {

        if (request.amount > 1000) {
            request.status = "HIGH";
        } else {
            request.status = "NORMAL";
        }

        System.out.println("Order status: " + request.status);

        if (request.id.equals("failed")) {
            throw new RuntimeException("Simulated failure for retry");
        }

        return new ResponseOrderDto(
                "000",
                "SUCCESS : Order has been processed successfully",
                request
        );
    }
}
