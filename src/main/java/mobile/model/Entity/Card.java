package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported=false)
@Document(collection = "card")
public class Card {
    @Id
    protected ObjectId id;
    protected ObjectId deckId;
    protected String front;
    protected String back;
    protected String IPA;
    protected String image;
    protected String audio;
    protected Set<String> tags;
    @CreatedDate
    protected Date createAt;
    @LastModifiedDate
    protected Date updateAt;
    protected Date lastReviewed;
    protected Date nextReview;
    protected int interval; // số ngày tới lần ôn tiếp theo
    protected double easeFactor; // độ dễ (giống Anki mặc định là 2.5)
    protected int repetition; // số lần ôn lại liên tiếp
    protected int lapses; // số lần quên
    protected int reviewCount; // số lần đã ôn
}
