package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
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

    public static void main(String[] args) throws SQLException {
        var app = getApp();
        app.start(7070);
    }

    private static String getDBUrl(boolean isTest) {
        if (isTest) {
            return "jdbc:h2:mem:project";
        }
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project");
//        "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;"
    }
    private static String getSchemaTemplate(boolean isTest) {
        if (isTest) {
            return "schemaH2.sql";
        }
        var isH2 = System.getenv().get("JDBC_DATABASE_URL") == null;
        return isH2 ? "schemaH2.sql" : "schema.sql";
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static Javalin getApp() throws SQLException {
        return getApp(false);
    }

    public static Javalin getApp(boolean isTest) throws SQLException {
        var hikariConfig = new HikariConfig();
        var dbUrl = getDBUrl(isTest);
        hikariConfig.setJdbcUrl(dbUrl);

        var dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;

        var schema = App.class.getClassLoader().getResourceAsStream(getSchemaTemplate(isTest));
        if (schema != null) {
            var sql = new BufferedReader(new InputStreamReader(schema))
                    .lines().collect(Collectors.joining("\n"));
            try (var connection = dataSource.getConnection();
                 var statement = connection.createStatement()) {
                try {
                    statement.execute(sql);
                } catch (SQLException e) {
                    System.out.println("Looks like table already exists!");
                }
            }
        } else {
            throw new SQLException("DB structure was not provided!");
        }

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });
        //root route
        app.get(NamedRoutes.rootPath(), RootController::root);
        //url related routes
        app.get(NamedRoutes.urlsPath(), UrlsController::urls);
        app.post(NamedRoutes.urlsPath(), UrlsController::add);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::url);

        return app;
    }
}
