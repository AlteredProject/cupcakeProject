package app.persistence;

import app.entities.Bottoms;
import app.entities.Toppings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CupcakeMapper {

    public static List<Toppings> getAllToppings(ConnectionPool conn) {
        List<Toppings> toppings = new ArrayList<>();
        String sql_toppings = "SELECT * FROM cupcake_toppings";


        try(
                Connection connection = conn.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql_toppings)
        ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("topping_id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                toppings.add(new Toppings(id, name, price));
            }
            return toppings;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Bottoms> getAllBottoms(ConnectionPool conn) {
        List<Bottoms> bottoms = new ArrayList<>();
        String sql_Bottoms = "SELECT * FROM cupcake_bottoms";

        try(
                Connection connection = conn.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql_Bottoms)
        ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("bottom_id");
                String name = rs.getString("name");
                int price = rs.getInt("price");
                bottoms.add(new Bottoms(id, name, price));
            }
            return bottoms;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
