package app.persistence;

import app.dto.OrderLineSummaryDTO;
import app.dto.OrderSummaryDTO;
import app.entities.Muffins;
import app.entities.Order;
import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderMapper {

    public static List<OrderSummaryDTO> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        ArrayList<Order> foundOrders = new ArrayList<>();

        String sql = """
        SELECT 
            orders.order_id, orders.order_date, orders.total_price, 
            users.email,
            order_lines.quantity,
            order_lines.unit_price,
            
            cupcake_toppings.name AS topping_name,
            cupcake_bottoms.name AS bottom_name
            
        FROM orders
        JOIN users ON orders.user_id = users.user_id
        JOIN order_lines ON orders.order_id = order_lines.order_id
        JOIN cupcake_toppings ON order_lines.topping_id = cupcake_toppings.topping_id
        JOIN cupcake_bottoms ON order_lines.bottom_id = cupcake_bottoms.bottom_id
        
        ORDER BY orders.order_id DESC;
        """;



        try (
            Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ResultSet rs = ps.executeQuery();

            Map<Integer, OrderSummaryDTO> orderMap = new LinkedHashMap<>();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");

                orderMap.putIfAbsent(orderId,
                    new OrderSummaryDTO(
                        orderId,
                        rs.getDate("order_date"),
                        rs.getDouble("total_price"),
                        rs.getString("email"),
                        new ArrayList<>()
                    ));

                OrderLineSummaryDTO line = new OrderLineSummaryDTO(
                    rs.getString("bottom_name"),
                    rs.getString("topping_name"),
                    rs.getDouble("unit_price"),
                    rs.getInt("quantity"),
                    rs.getDouble("unit_price")
                );

                orderMap.get(orderId).orderLines().add(line);
            }
            return new ArrayList<>(orderMap.values());
        } catch (SQLException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }

    public static void createOrderAndLines(User currentUser, List<Muffins> basket, ConnectionPool connectionPool) {
        String insertOrderSQL = "INSERT INTO orders (user_id, order_date, total_price) VALUES (?, CURRENT_DATE, ?) RETURNING order_id";

        double totalPrice = basket.stream()
                .mapToDouble(Muffins::totalPrice)
                .sum();

        try (Connection conn = connectionPool.getConnection()) {
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(insertOrderSQL)) {
                ps.setInt(1, currentUser.getUserId());
                ps.setDouble(2, totalPrice);
                ResultSet rs = ps.executeQuery();
                rs.next();
                orderId = rs.getInt("order_id");
            }
            String insertLine = "INSERT INTO order_lines (order_id, bottom_id, topping_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertLine)){
                for (Muffins muffin : basket) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, muffin.getBottom().getId());
                    ps.setInt(3, muffin.getTopping().getId());
                    ps.setInt(4, muffin.getQuantity());
                    ps.setDouble(5, muffin.totalPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Fejl ved oprettelse af order/orderLine: " + e.getMessage());
        }
    }

    public static void removeOrder(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String removeOrderSql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(removeOrderSql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved sletning af ordre", e.getMessage());
        }
    }
}
