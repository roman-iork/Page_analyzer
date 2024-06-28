package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;
import static io.javalin.rendering.template.TemplateUtil.model;
import java.net.URI;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UrlsController {
    public static void urls(Context ctx) throws SQLException {
        var urlsList = UrlsRepository.getUrls();
        var page = new UrlsPage(ctx.consumeSessionAttribute("flash"),
                ctx.consumeSessionAttribute("flashStatus"),
                urlsList);
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
            ctx.sessionAttribute("flashStatus", "alert-success");
            ctx.redirect(NamedRoutes.urlsPath());
        } else {
            var flash = "This URL already was added! \uD83D\uDE10";
            var flashStatus = "alert-warning";
            var urls = UrlsRepository.getUrls();
            var page = new UrlsPage(flash, flashStatus, urls);
            ctx.render("urls.jte", model("page", page));
        }
    }

    public static void url(Context ctx) {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        try {
            var url = UrlsRepository.find(id).orElseThrow(() -> {
                return new SQLException("No such URL in the list! \uD83E\uDD26");
            });
            var urlChecks = UrlCheckRepository.getChecks(id);
            var page = new UrlPage(url,
                    urlChecks,
                    ctx.consumeSessionAttribute("flash"),
                    ctx.consumeSessionAttribute("flashStatus"));
            ctx.render("url.jte", model("page", page));
        } catch (SQLException e) {
            ctx.status(404).result("Url with id = " + id + " not found");
        }
    }

    public static void check(Context ctx) throws SQLException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        var mayBeUrl = UrlsRepository.find(urlId);
        if (mayBeUrl.isEmpty()) {
            ctx.status(404).result("Url with id = " + urlId + " not found");
            return;
        }
        var urlName = mayBeUrl.get().getName();
        try {
            var response = Unirest.get(urlName).asString().getBody();
            var doc = Jsoup.parse(response);
            var status = ctx.status().getCode();
            var title = doc.select("title").first() == null ? "" : doc.select("title").first().text();
            var h1 = doc.select("h1").first() == null ? "" : doc.select("h1").first().text();
            var metaDescription = doc.select("meta[name='description'][content~=.*]").first();
            var description = metaDescription == null ? "" : metaDescription.attr("content");
            //create urlCheck instance and fill it with data
            var urlCheck = new UrlCheck();
            urlCheck.setStatusCode(status);
            urlCheck.setTitle(title);
            urlCheck.setH1(h1);
            urlCheck.setDescription(description);
            urlCheck.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            urlCheck.setUrlId(urlId);
            //save urlCheck instance to DB and redirect
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Check was successfully added! ✅");
            ctx.sessionAttribute("flashStatus", "alert-success");
            ctx.redirect(NamedRoutes.urlPath(urlId));
            Unirest.shutDown(); //is it necessary???
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Oops! The address is incorrect! \uD83D\uDE45");
            ctx.sessionAttribute("flashStatus", "alert-warning");
            ctx.redirect(NamedRoutes.urlPath(urlId));
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
