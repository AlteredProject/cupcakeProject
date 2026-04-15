package app.controllers;

import app.entities.Bottoms;
import app.entities.Muffins;
import app.entities.Toppings;
import app.entities.User;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> homePage(ctx, connectionPool));
        app.get("/order", ctx -> order(ctx, connectionPool));
        app.post("/basket/add", ctx -> addToBasket(ctx, connectionPool));
        app.get("/basket", ctx -> basket(ctx, connectionPool));
        app.post("/basket/buy", ctx -> buyBasket(ctx, connectionPool));
        app.get("/order/confirmation", ctx -> orderConfirmation(ctx, connectionPool));
    }

    public static void homePage(@NotNull Context ctx, ConnectionPool connectionPool){
        List<Toppings> toppings = CupcakeMapper.getAllToppings(connectionPool);
        List<Bottoms> bottoms = CupcakeMapper.getAllBottoms(connectionPool);

        List<Muffins> basket = ctx.sessionAttribute("basket");
        if (basket == null) { basket = new ArrayList<>(); }

        ctx.attribute("Toppings", toppings);
        ctx.attribute("Bottoms", bottoms);
        ctx.attribute("basket", basket);
        ctx.render("index.html");
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
