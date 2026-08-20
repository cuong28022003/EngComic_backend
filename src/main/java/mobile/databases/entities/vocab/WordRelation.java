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
    @JsonAlias({"word", "relatedText"})
    private String text;
    @JsonAlias({"relationType"})
    private String type;
    @JsonAlias({"meaning", "partOfSpeech"})
    private String pos;
    private String relatedCardId;

    public WordRelation(String text, String type, String pos) {
        this.text = text;
        this.type = type;
        this.pos = pos;
    }
}

