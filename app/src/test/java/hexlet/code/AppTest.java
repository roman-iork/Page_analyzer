package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlsRepository;
import io.javalin.Javalin;
import static io.javalin.testtools.JavalinTest.test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import static hexlet.code.App.getApp;
import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {
    private static Javalin app;
    private static MockWebServer server = new MockWebServer();

    @BeforeAll
    public static void setUpServers() throws IOException {
        server.enqueue(new MockResponse()
                .setBody("Some text"
                + "<title>Check title</title>"
                + "Some text"
                + "<meta name=\"description\" content=\"Check description\"/>"
                + "Some text\n"
                + "<h1>Check h1</h1>\n"
                + "Some text"
        ));
        server.start();
    }

    @AfterAll
    public static void stopServer() throws IOException {
        server.shutdown();
    }

    @BeforeEach
    public void startApp() throws SQLException {
        app = getApp(true);
    }

    @Test
    public void testRoot() {
        test(app, (srv, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Page analyzer",
                    "Check SEO information for free",
                    " during studying");
        });
    }

    @Test
    public void testUrls() throws SQLException {
        var url1 = new Url("http://url1.io", Timestamp.valueOf("2024-06-23 11:00:00"));
        var url2 = new Url("http://url2.io", Timestamp.valueOf("2024-06-23 12:00:00"));
        var url3 = new Url("http://url3.io", Timestamp.valueOf("2024-06-23 13:00:00"));
        UrlsRepository.save(url1);
        UrlsRepository.save(url2);
        UrlsRepository.save(url3);
        test(app, (srv, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("http://url1.io", "http://url2.io", "http://url3.io");
        });
    }

    @Test
    public void testUrl() throws SQLException {
        var url = new Url("http://url.io", Timestamp.valueOf("2024-06-23 11:00:00"));
        UrlsRepository.save(url);
        test(app, (srv, client) -> {
            var response = client.get("/urls/1");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("http://url.io", "2024-06-23", "11:00", "Check chronology");
        });
    }

    @Test
    public void testPostUrl() throws SQLException {
        var requestBody = "url=http://url.io";
        var id = "1";
        test(app, (srv, client) -> {
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(id, "http://url.io");
        });
        var url = UrlsRepository.find(1L).orElseThrow();
        assertThat(url.getName()).isEqualTo("http://url.io");
        assertThat(UrlsRepository.getUrls().size()).isEqualTo(1);
    }

    @Test
    public void testPostUrlDuplicate() throws SQLException {
        var requestBody = "url=http://url.io";
        test(app, (srv, client) -> {
            client.post("/urls", requestBody);
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("This URL already was added!");
        });
        assertThat(UrlsRepository.getUrls().size()).isEqualTo(1);
    }

    @Test
    public void testPostUrlWrong() throws SQLException {
        var requestBody = "url=hhhh://url.io";
        test(app, (srv, client) -> {
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Wrong URL!");
        });
        assertThat(UrlsRepository.getUrls().size()).isEqualTo(0);
    }

    @Test
    public void testUrlNotExisting() throws SQLException {
        var url = new Url("http://url.io", Timestamp.valueOf("2024-06-23 11:00:00"));
        UrlsRepository.save(url);
        test(app, (srv, client) -> {
            var response = client.get("/urls/2");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testUrlExtracting() throws SQLException {
        var requestBody = "url=http://url.io/5/checks";
        test(app, (srv, client) -> {
            client.post("/urls", requestBody);
            var response = client.get("/urls/1");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).doesNotContain("/5/checks");
        });
        assertThat(UrlsRepository.getUrls().size()).isEqualTo(1);
    }

    @Test
    public void testUrlCheck() {
        var baseUrl = server.url("/").toString();
        var requestBody = "url=" + baseUrl;
        test(app, (srv, client) -> {
            client.post("/urls", requestBody);
            assertThat(UrlCheckRepository.getChecks(1L).size()).isEqualTo(0);
            var response = client.post("/urls/1/checks");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Check title", "Check description", "Check h1");
            assertThat(UrlCheckRepository.getChecks(1L).size()).isEqualTo(1);
        });

    }
}
