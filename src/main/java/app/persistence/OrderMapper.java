package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;

public class OrderMapper {
    public static void createOrder(int userId, Date orderDate, double totalPrice, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "insert into orders (user_id, order_date, total_price) values (?,?,?)";

        try (
            Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
        ) {
            ps.setInt(1, userId);
            ps.setDate(2, orderDate);
            ps.setDouble(3, totalPrice);


            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1 ) {
                throw new DatabaseException("Fejl ved oprettelse af ny ordre");
            }

        } catch (SQLException e) {
            String msg = "Der er sket en fejl. Prøv igen";
            if (e.getMessage().startsWith("ERROR: duplicate key value ")) {
                msg = "en ordre med dette orderId findes allerede.";
            }
            throw new DatabaseException(msg, e.getMessage());
        }
    }

    public static ArrayList<Order> getOrders(ConnectionPool connectionPool) throws DatabaseException {
        ArrayList<Order> foundOrders = new ArrayList<>();

        String sql = "select * from orders";

        try (
            Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int userId = rs.getInt("user_id");
                Date orderDate = rs.getDate("order_date");
                double totalPrice = rs.getDouble("total_price");
                foundOrders.add(new Order(orderId, userId, orderDate, totalPrice));
            }
            return foundOrders;
        } catch (SQLException e) {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }
}
