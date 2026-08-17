package mobile.apis.card.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.databases.entities.card.ExampleSentence;
import mobile.databases.entities.card.WordRelation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDto {
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
    protected String definitionEn;
    protected String usageNote;
    protected String topic;

    protected List<ExampleSentence> examples = new ArrayList<>();
    protected List<WordRelation> relations = new ArrayList<>();

    protected String personalNote;
    protected String myExample;

    @JsonProperty("isFavorite")
    protected boolean isFavorite;

    protected int stage;
    protected String status;
    protected int interval;
    protected double easeFactor;
    protected int repetition;
    protected int lapses;
    protected int wrongCount;
    protected int reviewCount;
    protected String lastReviewed;
    protected String nextReview;

    protected String createAt;
    protected String updateAt;
}
