package mobile.databases.entities.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported = false)
@Document(collection = "card")
public class CardEntity {
    @MongoId(FieldType.OBJECT_ID)
    protected String id;
    protected String userId;
    protected String deckId;

    // --- Static Content ---
    protected String front;              // word or phrase
    protected String back;               // Vietnamese meaning
    protected String ipa;
    protected String partOfSpeech;
    protected String definitionEn;
    protected String usageNote;
    protected String topic;
    protected String image;
    protected String audio;

    protected List<ExampleSentence> examples = new ArrayList<>();
    protected List<WordRelation> relations = new ArrayList<>();

    // --- Personal & Tags ---
    protected Set<String> tags;
    protected String personalNote;
    protected String myExample;
    protected boolean isFavorite = false;

    // --- SRS Progress ---
    protected int stage = 0;             // 0-5
    protected String status = "new";     // new / learning / mature / leech
    protected int interval = 0;          // days until next review
    protected double easeFactor = 2.5;   // SM-2 default ease factor
    protected int repetition = 0;        // consecutive correct count
    protected int lapses = 0;            // times forgotten
    protected int wrongCount = 0;        // cumulative wrong count for leech detection
    protected int reviewCount = 0;       // total review sessions
    protected Date lastReviewed;
    protected Date nextReview;
    protected List<String> seenExampleIds = new ArrayList<>();

    // --- Timestamps ---
    @CreatedDate
    protected Date createAt;
    @LastModifiedDate
    protected Date updateAt;
}
