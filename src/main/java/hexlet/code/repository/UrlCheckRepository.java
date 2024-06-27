package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class UrlCheckRepository extends BaseRepository {
    //save
    public static void save(UrlCheck urlCheck) throws SQLException {
        if (urlCheck.getId() == null) {
            var sql = "INSERT INTO url_checks "
                    + "(status_code, title, h1, description, created_at, url_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (var connection = dataSource.getConnection();
                 var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, urlCheck.getStatusCode());
                preparedStatement.setString(2, urlCheck.getTitle());
                preparedStatement.setString(3, urlCheck.getH1());
                preparedStatement.setString(4, urlCheck.getDescription());
                preparedStatement.setTimestamp(5, urlCheck.getCreatedAt());
                preparedStatement.setLong(6, urlCheck.getUrlId());
                preparedStatement.executeUpdate();
                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    var id = generatedKeys.getLong("id");
                    urlCheck.setId(id);
                } else {
                    throw new SQLException("DB didn't return any index");
                }
            }
        } else {
            throw new SQLException("Such urlCheck id already exists!");
        }
    }
    //getChecks
    public static List<UrlCheck> getChecks(Long urlId) throws SQLException {
        var urlChecks = new LinkedList<UrlCheck>();
        var sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC";
        try (var connection = dataSource.getConnection();
                var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, urlId);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                var urlCheck = new UrlCheck(urlId);
                urlCheck.setId(resultSet.getLong("id"));
                urlCheck.setStatusCode(resultSet.getInt("status_code"));
                urlCheck.setTitle(resultSet.getString("title"));
                urlCheck.setH1(resultSet.getString("h1"));
                urlCheck.setDescription(resultSet.getString("description"));
                urlCheck.setCreatedAt(resultSet.getTimestamp("created_at"));
                urlChecks.add(urlCheck);
            }
        }
        return urlChecks;
    }
    //findLast
    public static Optional<UrlCheck> findLast(Long urlId) {
        try {
            var urlChecks = getChecks(urlId);
            if (!urlChecks.isEmpty()) {
                return Optional.of(urlChecks.getFirst());
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
