package mobile.databases.entities.vocab;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExampleSentence {
    private String id;
    @JsonAlias({"en", "sentence"})
    private String en;
    @JsonAlias({"vi", "translation"})
    private String vi;
    @JsonAlias({"context", "formality"})
    private String context;
}
