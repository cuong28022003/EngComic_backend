package mobile.apis.vocab.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.databases.entities.vocab.ExampleSentence;
import mobile.databases.entities.vocab.WordRelation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateCardRequest {
    @JsonAlias({"front"})
    protected String word;
    @JsonAlias({"back", "meaning_vi", "meaningVi"})
    protected String meaning;

    public String getFront() {
        return word;
    }
    public String getBack() {
        return meaning;
    }

    @JsonProperty("IPA")
    @JsonAlias({"IPA", "ipa"})
    protected String ipa;

    protected String image;
    protected String audio;
    protected Set<String> tags = new HashSet<>();
    protected String userId;
    protected String deckId;

    protected String partOfSpeech;
    protected String definitionEn;
    protected String usageNote;
    protected String topic;

    protected List<ExampleSentence> examples = new ArrayList<>();
    protected List<WordRelation> relations = new ArrayList<>();

    protected String personalNote;
    protected String myExample;
    protected boolean isFavorite;

    public String getIPA() {
        return ipa;
    }

    public void setIPA(String ipa) {
        this.ipa = ipa;
    }
}

