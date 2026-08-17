package mobile.databases.entities.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordRelation {
    private String relatedText;     // "decide", "reach a decision", "choice"
    private String relationType;    // "family" | "collocation" | "synonym"
    private String pos;             // only for family: "noun", "verb", "adjective", "adverb"
    private String relatedCardId;   // hex string of linked Card id if matched, otherwise null
}
