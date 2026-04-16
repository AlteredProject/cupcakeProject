package app.dto;

public record OrderLineSummaryDTO (
    String toppingName,
    String bottomName,
    double unitPrice,
    int quantity,
    double lineTotal
    ) {}
