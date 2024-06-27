package hexlet.code.utils;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.App;

public class Utils {

    public static String getDBUrl(boolean isTest) {
        if (isTest) {
            return "jdbc:h2:mem:project";
        }
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
    }

    public static String getSchemaTemplate(boolean isTest) {
        if (isTest) {
            return "schemaH2.sql";
        }
        var isH2 = System.getenv().get("JDBC_DATABASE_URL") == null;
        return isH2 ? "schemaH2.sql" : "schema.sql";
    }

    public static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }
}
