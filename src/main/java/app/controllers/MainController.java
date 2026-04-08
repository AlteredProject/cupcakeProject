package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;

public class MainController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("index.html"));
    }
}
