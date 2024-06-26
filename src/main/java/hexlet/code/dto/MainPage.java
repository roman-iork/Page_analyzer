package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MainPage {
    private String flash;
    private String flashStatus;
    private String canBeUrl;
}
