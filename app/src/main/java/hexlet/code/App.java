package hexlet.code;

import hexlet.code.utils.Utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class App {
    //entrance point -> create app and start it
    public static void main(String[] args) throws SQLException {
        var app = getApp();
        app.start(7070);
    }
    //getApp with parameter -> to choose app for tests because of different DBs and table structures
    public static Javalin getApp() throws SQLException {
        return getApp(false);
    }
    public static Javalin getApp(boolean isTest) throws SQLException {
        //configuring pool of connections
        var hikariConfig = new HikariConfig();
        var dbUrl = Utils.getDBUrl(isTest);
        hikariConfig.setJdbcUrl(dbUrl);
        //creating source of connections
        var dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;
        //creating table structure
        var schema = App.class.getClassLoader().getResourceAsStream(Utils.getSchemaTemplate(isTest));
        if (schema != null) {
            var sql = new BufferedReader(new InputStreamReader(schema))
                    .lines().collect(Collectors.joining("\n"));
            try (var connection = dataSource.getConnection();
                 var statement = connection.createStatement()) {
                try {
                    statement.execute(sql);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            throw new SQLException("DB structure was not provided!");
        }
        //creating javalin app and configuring logging and template engine
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(Utils.createTemplateEngine()));
        });
        //root route
        app.get(NamedRoutes.rootPath(), RootController::root);
        //url related routes
        app.get(NamedRoutes.urlsPath(), UrlsController::urls);
        app.post(NamedRoutes.urlsPath(), UrlsController::add);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::url);
        app.post(NamedRoutes.checkPath("{id}"), UrlsController::check);
        //ready app
        return app;
    }
}
