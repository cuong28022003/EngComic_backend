package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "word_family")
public class WordFamily {
    @Id
    @JsonIgnore
    private ObjectId id;
    private String rootWord;
    private List<FamilyMember> members = new ArrayList<>();

    // Compatibility fields
    private String noun;
    private String verb;
    private String adjective;
    private String adverb;
}
