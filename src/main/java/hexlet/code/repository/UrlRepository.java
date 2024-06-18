package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UrlRepository extends BaseRepository {
    public static List<Url> getUrls() throws SQLException {
        var sql = "SELECT * FROM urls";
        var urls = new ArrayList<Url>();
        try (var connection = dataSource.getConnection();
                var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(name, createdAt);
                url.setId(id);
                urls.add(url);
            }
        }
        return urls;
    }

    public static void save(Url url) throws SQLException {
        if (url.getId() == null) {
            var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
            try (var connection = dataSource.getConnection();
                 var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, url.getName());
                preparedStatement.setTimestamp(2, url.getCreatedAt());
                preparedStatement.executeUpdate();
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    var id = generatedKeys.getLong("id");
                    url.setId(id);
                } else {
                    throw new SQLException("DB didn't return any index");
                }
            }
        } else {
            throw new SQLException("Such url id already exists!");
        }
    }
}
