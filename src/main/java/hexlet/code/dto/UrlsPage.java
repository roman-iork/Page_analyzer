package hexlet.code.dto;

import hexlet.code.model.Url;
import io.javalin.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class UrlsPage {
    private String flash;
    private String status;
    private List<Url> urls;
}
