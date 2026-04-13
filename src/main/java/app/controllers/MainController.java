package app.controllers;

import app.entities.Bottoms;
import app.entities.Toppings;
import app.persistence.ConnectionPool;
import app.persistence.CupcakeMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> homePage(ctx, connectionPool));
    }

    public static void homePage(@NotNull Context ctx, ConnectionPool connectionPool){
        List<Toppings> toppings = CupcakeMapper.getAllToppings(connectionPool);
        List<Bottoms> bottoms = CupcakeMapper.getAllBottoms(connectionPool);
        ctx.attribute("Toppings", toppings);
        ctx.attribute("Bottoms", bottoms);
        ctx.render("index.html");
    }
}
