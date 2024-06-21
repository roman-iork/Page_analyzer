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
//        var regex = "^http(:|s:)\\/\\/[\\w-]*\\.[a-z?%&=]*(:\\d*)*(\\/[\\w?%&=]*)*$";
//        var text = "https://www.some-domain.org/example/path&";
//        var regexPattern = Pattern.compile(regex);
//        var matcher = regexPattern.matcher(text);
//        if (matcher.find()) {
//            System.out.println(text.substring(matcher.start(), matcher.end()));
//        } else {
//            System.out.println("No matches!");
//        }
    }

    private static String getDBUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "");
//        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
    }

//    private static Integer getPort() {
//        var port = System.getenv().getOrDefault("PORT", "7070");
//        return Integer.parseInt(port);
//    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static Javalin getApp() throws SQLException {
        var hikariConfig = new HikariConfig();
        var dbUrl = getDBUrl();
        hikariConfig.setJdbcUrl(dbUrl);

        var dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.dataSource = dataSource;

        var schema = App.class.getClassLoader().getResourceAsStream("schema.sql");
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
