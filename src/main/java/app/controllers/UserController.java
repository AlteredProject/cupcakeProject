package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import app.services.Validator;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

public class UserController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/createUser", ctx -> ctx.render("createUser.html"));
        app.post("/createUser", ctx -> registerUser(ctx, connectionPool));

        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> login(ctx, connectionPool));


    }

    public static void registerUser(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        try {
            String validationError = Validator.validateUser(email, password);
            if (validationError == null) {
                String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
                UserMapper.createUser(email, hashPassword, connectionPool);
                MainController.homePage(ctx, connectionPool);
            } else {
                ctx.attribute("msg", validationError);
                ctx.render("createUser.html");
            }
        } catch (DatabaseException e) {
            ctx.attribute("msg", e.getMessage());
            ctx.render("createUser.html");
        }
    }

    public static void login (Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        try {
            String validationError = Validator.validateUser(email, password);
            if (validationError != null) {
                ctx.attribute("msg", validationError);
                ctx.render("login.html");
                return;
            }

            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            MainController.homePage(ctx, connectionPool);
        } catch (DatabaseException e) {
            ctx.attribute("msg", e.getMessage());
            ctx.render("login.html");
        }
    }
}
