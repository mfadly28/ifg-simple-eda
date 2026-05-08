package id.co.fadly.controller;

import id.co.fadly.model.RequestOrderDto;
import id.co.fadly.model.ResponseOrderDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProducerController {

    @Channel("orders-out")
    Emitter<RequestOrderDto> orderEmitter;

    @POST
    public Response createOrder(RequestOrderDto request) {
        orderEmitter.send(request);

        ResponseOrderDto responseOrder = new ResponseOrderDto("000","SUCCESS : Order has been processed successfully", request);

        return Response.accepted(responseOrder).build();
    }
}