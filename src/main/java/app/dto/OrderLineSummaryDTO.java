package app.dto;

public record OrderLineSummaryDTO (
    String toppingName,
    double toppingPrice,
    String bottomName,
    double bottomPrice,
    int quantity,
    double lineTotal
) {}
