package mobile.databases.entities.vocab;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
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
    protected String word;
    protected String meaning;
    protected String ipa;
    protected String partOfSpeech;
    protected String definitionEn;
    protected String usageNote;
    protected String topic;
    protected String image;
    protected String audio;
    protected List<WordRelation> relations = new ArrayList<>();
    protected List<WordUsage> usages = new ArrayList<>();
    protected String comparisonGroup;

    // --- Personal & Tags ---
    protected Set<String> tags;
    protected String personalNote;
    protected String myExample;
    protected boolean isFavorite = false;

    // --- SRS Progress ---
    protected int stage = 0;
    protected int masteryLevel = 1;
    protected int confidenceScore = 0;
    protected String memoryTip;
    protected CardExercisePackage exercisePackage;
    protected String status = "new";
    protected int interval = 0;
    protected double easeFactor = 2.5;
    protected int repetition = 0;
    protected int lapses = 0;
    protected int wrongCount = 0;
    protected int reviewCount = 0;
    protected Date lastReviewed;
    protected Date nextReview;
    protected List<String> seenExampleIds = new ArrayList<>();

    // --- Timestamps ---
    @CreatedDate
    protected Date createAt;
    @LastModifiedDate
    protected Date updateAt;
}
