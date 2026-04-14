package app.dto;

import java.sql.Date;

public record OrderSummaryDTO(
    int orderId,
    Date orderDate,
    double totalPrice
) {}
