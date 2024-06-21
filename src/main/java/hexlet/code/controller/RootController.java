package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import io.javalin.http.Context;
import static io.javalin.rendering.template.TemplateUtil.model;

public class RootController {
    public static void root(Context ctx) {
        var page = new MainPage(null, null, null);
        ctx.render("main.jte", model("page", page));
    }
}
