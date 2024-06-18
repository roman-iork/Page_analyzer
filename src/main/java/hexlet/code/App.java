package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) throws SQLException {
        var app = getApp();
        app.start(7070);
    }

    private static String getDBUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "");
//        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
    }

//    private static Integer getPort() {
//        var port = System.getenv().getOrDefault("PORT", "7070");
//        return Integer.parseInt(port);
//    }

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

        var app = Javalin.create(config -> config.bundledPlugins.enableDevLogging());

        app.get("/", ctx -> ctx.result(UrlRepository.getUrls().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"))));
        app.get("/create", ctx -> {
            var name = ctx.queryParam("name");
            var createdAt = ctx.queryParam("created");
            if (createdAt != null) {
                var url = new Url(name, Timestamp.valueOf(createdAt));
                UrlRepository.save(url);
            } else {
                throw new SQLException("Created_at not indicated!");
            }
            ctx.redirect("/");
        });

        return app;
    }
}
