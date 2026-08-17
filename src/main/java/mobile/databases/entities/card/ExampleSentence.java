package mobile.databases.entities.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExampleSentence {
    private String sentence;
    private String translation;
    private String formality; // "formal" | "informal" | "written"
    private String source;

    public ExampleSentence(String sentence, String translation, String source) {
        this.sentence = sentence;
        this.translation = translation;
        this.source = source;
    }
}
