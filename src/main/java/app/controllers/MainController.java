package app.controllers;

import app.entities.Bottoms;
import app.entities.Muffins;
import app.entities.Toppings;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> homePage(ctx, connectionPool));
        app.get("/order", ctx -> order(ctx, connectionPool));
    }

    private static void homePage(@NotNull Context ctx, ConnectionPool connectionPool){
        List<Toppings> toppings = CupcakeMapper.getAllToppings(connectionPool);
        List<Bottoms> bottoms = CupcakeMapper.getAllBottoms(connectionPool);
        ctx.attribute("Toppings", toppings);
        ctx.attribute("Bottoms", bottoms);
        ctx.render("index.html");
    }

    private static void order (@NotNull Context ctx, ConnectionPool connectionPool){
        ctx.render("order.html");
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
}
