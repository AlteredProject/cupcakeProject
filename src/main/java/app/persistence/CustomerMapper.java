package app.persistence;

import app.dto.CustomerInfoDTO;
import app.dto.OrderSummaryDTO;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerMapper {
    public static List<CustomerInfoDTO> getCustomerInfo(ConnectionPool connectionPool) throws DatabaseException {

        String sql = """
            SELECT users.user_id, users.email, users.credit_balance, orders.order_id, orders.order_date, orders.total_price
            FROM users LEFT JOIN orders ON users.user_id = orders.user_id
            ORDER BY users.email ASC, orders.order_date DESC
            """;

        try (
            Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ResultSet rs = ps.executeQuery();

            Map<Integer, String> emailMap = new LinkedHashMap<>();
            Map<Integer, Double> balanceMap = new LinkedHashMap<>();
            Map<Integer, List<OrderSummaryDTO>> ordersMap = new LinkedHashMap<>();

            while (rs.next()) {
                int userId = rs.getInt("user_id");

                emailMap.putIfAbsent(userId, rs.getString("email"));
                balanceMap.putIfAbsent(userId, rs.getDouble("credit_balance"));
                ordersMap.putIfAbsent(userId, new ArrayList<>());

                int orderId = rs.getInt("order_id");

                if (!rs.wasNull()) {

                    OrderSummaryDTO order = new OrderSummaryDTO(
                        orderId,
                        rs.getDate("order_date"),
                        rs.getDouble("total_price")
                    );

                    ordersMap.get(userId).add(order);
                }
            }

            List<CustomerInfoDTO> result = new ArrayList<>();

            for (Integer userId : emailMap.keySet()) {
                result.add(new CustomerInfoDTO(
                    emailMap.get(userId),
                    balanceMap.get(userId),
                    ordersMap.get(userId)
                ));
            }

            return result;

        } catch (SQLException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }
}
