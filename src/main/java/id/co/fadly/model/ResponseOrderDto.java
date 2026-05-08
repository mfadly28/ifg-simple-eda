package id.co.fadly.model;

public class ResponseOrderDto {

    public String responseCode;
    public String responseMessage;
    public RequestOrderDto orders;

    public ResponseOrderDto() {}

    public ResponseOrderDto(String responseCode, String responseMessage, RequestOrderDto orders) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.orders = orders;
    }
}
