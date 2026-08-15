package mobile.model.payload.response.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import mobile.model.Entity.ExampleSentence;
import mobile.model.Entity.WordFamily;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    protected String id;
    protected String userId;
    protected String deckId;
    protected String front;
    protected String back;

    @JsonProperty("ipa")
    protected String ipa;

    protected String image;
    protected String audio;
    protected Set<String> tags;
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

    @JsonProperty("isFavorite")
    protected boolean isFavorite;

    protected String masteryStatus;
    protected String createAt;
    protected String updateAt;
}
