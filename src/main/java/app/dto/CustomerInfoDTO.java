package app.dto;

import java.util.List;

public record CustomerInfoDTO (
    String email,
    double balance,
    List<OrderSummaryDTO> orders
){
}
