package app.dto;

import java.sql.Date;
import java.util.List;

public record OrderSummaryDTO(
    int orderId,
    Date orderDate,
    double totalPrice,
    String email,
    List<OrderLineSummaryDTO> orderLines
) {}
