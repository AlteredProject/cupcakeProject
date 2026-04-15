package app.controllers;

import app.dto.CustomerInfoDTO;
import app.dto.OrderSummaryDTO;
import app.entities.Bottoms;
import app.entities.Toppings;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import app.persistence.CustomerMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> homePage(ctx, connectionPool));

        app.get("/adminCustomersView", ctx -> adminCustomersPage(ctx, connectionPool));
        app.get("/adminOrdersView", ctx -> adminOrdersPage(ctx, connectionPool));
    }

    public static void homePage(@NotNull Context ctx, ConnectionPool connectionPool) {
        List<Toppings> toppings = CupcakeMapper.getAllToppings(connectionPool);
        List<Bottoms> bottoms = CupcakeMapper.getAllBottoms(connectionPool);
        ctx.attribute("Toppings", toppings);
        ctx.attribute("Bottoms", bottoms);
        ctx.render("index.html");
    }

    public static void adminCustomersPage(@NotNull Context ctx, ConnectionPool connectionPool) {
        try {
            List<CustomerInfoDTO> customers = CustomerMapper.getCustomerInfo(connectionPool);

            ctx.attribute("customers", customers);
            ctx.render("customers-admin.html");
        } catch (DatabaseException e) {
            ctx.result("Fejl ved indhentning af kunder");
        }
    }

    public static void adminOrdersPage(@NotNull Context ctx, ConnectionPool connectionPool) {
        try {
           List<OrderSummaryDTO> orderSummaries = OrderMapper.getAllOrders(connectionPool);

           ctx.attribute("orders", orderSummaries);
           ctx.render("orders-admin.html");
        } catch (DatabaseException e) {
            ctx.result("Fejl ved indhentning af ordrer");
        }
        ctx.render("orders-admin.html");
    }
}
