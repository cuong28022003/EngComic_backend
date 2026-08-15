package mobile.model.payload.request.card;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mobile.model.Entity.ExampleSentence;
import mobile.model.Entity.WordFamily;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateCardRequest {
    protected String front;
    protected String back;

    @JsonProperty("IPA")
    @JsonAlias({"IPA", "ipa"})
    protected String ipa;

    protected String image;
    protected String audio;
    protected Set<String> tags = new HashSet<>();
    protected String userId;
    protected String deckId;

    protected String partOfSpeech;
    protected String level;
    protected List<ExampleSentence> examples;
    protected List<String> collocations;
    protected List<String> synonyms;
    protected List<String> antonyms;
    protected WordFamily wordFamily;
    protected List<String> commonMistakes;
    protected String personalNote;
    protected String myExample;
    protected boolean isFavorite;
    protected String masteryStatus;

    public String getIPA() {
        return ipa;
    }

    public void setIPA(String ipa) {
        this.ipa = ipa;
    }
}
