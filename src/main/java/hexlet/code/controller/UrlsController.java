package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;

import static io.javalin.rendering.template.TemplateUtil.model;

import java.net.URI;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UrlsController {
    public static void urls(Context ctx) throws SQLException {
        var urlsList = UrlsRepository.getUrls();
        var page = new UrlsPage(ctx.consumeSessionAttribute("flash"), ctx.consumeSessionAttribute("status"), urlsList);
        ctx.render("urls.jte", model("page", page));
    }

    public static void add(Context ctx) throws SQLException {
        var canBeUrl = ctx.formParam("url");
        if (!isUrl(canBeUrl)) {
            var page = new MainPage("Wrong URL!", "alert-warning", canBeUrl);
            ctx.render("main.jte", model("page", page));
            return;
        }
        var urlString = toUrl(canBeUrl);
        if (UrlsRepository.isUnique(urlString)) {
            var createdAt = Timestamp.valueOf(LocalDateTime.now());
            Url url = new Url(urlString, createdAt);
            UrlsRepository.save(url);
            ctx.sessionAttribute("flash", "URL was successfully added \uD83D\uDCAA");
            ctx.sessionAttribute("status", "alert-success");
            ctx.redirect(NamedRoutes.urlsPath());
        } else {
            var flash = "This URL already was added! \uD83D\uDE10";
            var status = "alert-warning";
            var urls = UrlsRepository.getUrls();
            var page = new UrlsPage(flash, status, urls);
            ctx.render("urls.jte", model("page", page));
        }
    }

    public static void url(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        try {
            var url = UrlsRepository.find(id).orElseThrow(() -> new SQLException("No such URL! \uD83E\uDD26"));
            var page = new UrlPage(url);
            ctx.render("url.jte", model("page", page));
        } catch (SQLException e) {
            var urls = UrlsRepository.getUrls();
            var page = new UrlsPage(e.getMessage(), "alert-warning", urls);
            ctx.render("urls.jte", model("page", page)).status(404);
        }
    }

    public static String toUrl(String canBeUrl) {
        try {
            var uri = new URI(canBeUrl);
            return uri.toURL().getProtocol() + "://" + uri.toURL().getAuthority();
        } catch (Exception e) {
            throw new RuntimeException("For some reason isUrl method parsed this url???");
        }
    }

    public static boolean isUrl(String canBeUrl) {
        try {
            toUrl(canBeUrl);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
