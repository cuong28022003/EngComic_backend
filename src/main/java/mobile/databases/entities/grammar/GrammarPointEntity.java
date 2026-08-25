package mobile.databases.entities.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "grammar_points")
public class GrammarPointEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String topic;

    @Indexed
    private String category;

    private String shortRule;

    private String structure;

    private List<String> signalWords;

    private String commonMistake;

    private List<GrammarExample> examples;

    private List<String> searchKeywords;

    private Date createdAt;

    private Date updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarExample {
        private String text;
        private String note;
    }
}
