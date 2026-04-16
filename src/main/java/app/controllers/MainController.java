package app.controllers;

import app.dto.CustomerInfoDTO;
import app.dto.OrderSummaryDTO;
import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import app.persistence.CustomerMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> homePage(ctx, connectionPool));

        app.get("/adminCustomersView", ctx -> adminCustomersPage(ctx, connectionPool));
        app.get("/adminOrdersView", ctx -> adminOrdersPage(ctx, connectionPool));

        app.get("/order", ctx -> order(ctx, connectionPool));
        app.post("/order/delete", ctx -> removeOrder(ctx, connectionPool));

        app.post("/basket/add", ctx -> addToBasket(ctx, connectionPool));
        app.get("/basket", ctx -> basket(ctx, connectionPool));
        app.post("/basket/buy", ctx -> buyBasket(ctx, connectionPool));
        app.get("/order/confirmation", ctx -> orderConfirmation(ctx, connectionPool));
        app.post("/basket/remove", ctx -> removeFromBasket(ctx, connectionPool));
    }

    public static void homePage(@NotNull Context ctx, ConnectionPool connectionPool) {
        List<Toppings> toppings = CupcakeMapper.getAllToppings(connectionPool);
        List<Bottoms> bottoms = CupcakeMapper.getAllBottoms(connectionPool);

        List<Muffins> basket = ctx.sessionAttribute("basket");
        if (basket == null) { basket = new ArrayList<>(); }

        ctx.attribute("Toppings", toppings);
        ctx.attribute("Bottoms", bottoms);
        ctx.attribute("basket", basket);
        ctx.render("index.html");
    }

    public static void adminCustomersPage(@NotNull Context ctx, ConnectionPool connectionPool) {
        try {
            List<CustomerInfoDTO> customers = CustomerMapper.getCustomerInfo(connectionPool);

            ctx.attribute("customers", customers);
            renderWithBasket(ctx,"customers-admin.html");
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
        renderWithBasket(ctx, "orders-admin.html");
    }

    public static void removeOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        int orderId = Integer.parseInt(ctx.formParam("orderId"));
        OrderMapper.removeOrder(orderId, connectionPool);
        ctx.redirect("/adminOrdersView");
    }

    private static void order (@NotNull Context ctx, ConnectionPool connectionPool){
        renderWithBasket(ctx, "order.html");
    }

    private static void basket (@NotNull Context ctx, ConnectionPool connectionPool){
        renderWithBasket(ctx, "basket.html");
    }

    private static void orderConfirmation (@NotNull Context ctx, ConnectionPool connectionPool){
        renderWithBasket(ctx, "confirmation.html");
    }

    private static void addToBasket (@NotNull Context ctx, ConnectionPool connectionPool) {

        List<Muffins> basket = ctx.sessionAttribute("basket");
        if (basket == null) { basket = new ArrayList<>(); }

        int bottomId = Integer.parseInt(ctx.formParam("bottom"));
        int toppingId = Integer.parseInt(ctx.formParam("topping"));
        int quantity = Integer.parseInt(ctx.formParam("quantity"));

        Bottoms bottom = CupcakeMapper.getBottomById(bottomId, connectionPool);
        Toppings toppping = CupcakeMapper.getToppingById(toppingId, connectionPool);

        basket.add(new Muffins(bottom, toppping, quantity));
        ctx.sessionAttribute("basket", basket);

        ctx.redirect("/");
    }

    private static void removeFromBasket (@NotNull Context ctx, ConnectionPool connectionPool) {
        int index = Integer.parseInt(ctx.formParam("index"));
        List<Muffins> basket = ctx.sessionAttribute("basket");

        if (basket != null && index >= 0 && index < basket.size()){
            basket.remove(index);
            ctx.sessionAttribute("basket", basket);
        }

        ctx.redirect("/basket");
    }

    private static void renderWithBasket(Context ctx, String site) {
        List<Muffins> basket = ctx.sessionAttribute("basket");
        if (basket == null) { basket = new ArrayList<>(); }

        double totalPrice = basket.stream()
                .mapToDouble(Muffins::totalPrice)
                .sum();

        ctx.attribute("basket", basket);
        ctx.attribute("totalPrice", totalPrice);
        ctx.render(site);
    }

    private static void buyBasket(@NotNull Context ctx, ConnectionPool connectionPool){
        User currentUser = ctx.sessionAttribute("currentUser");
        List<Muffins> basket = ctx.sessionAttribute("basket");

        if (currentUser!=null && basket!=null) {
            OrderMapper.createOrderAndLines(currentUser, basket, connectionPool);

            ctx.sessionAttribute("basket", null);
            ctx.redirect("/order/confirmation");
        } else {
            ctx.redirect("/");
        }
    }
}