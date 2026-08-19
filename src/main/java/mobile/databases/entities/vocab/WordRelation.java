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
public class WordRelation {
    @JsonAlias({"type", "relationType"})
    private String type;
    @JsonAlias({"word", "relatedText"})
    private String word;
    @JsonAlias({"meaning", "pos"})
    private String meaning;
}
