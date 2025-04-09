package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported=false)
@Document(collection = "cardReview")
public class CardReview {
    @Id
    protected ObjectId id;
    protected ObjectId userId;
    protected ObjectId cardId;
    protected Date lastReviewed;
    protected Date nextReview;
    protected int interval; // số ngày tới lần ôn tiếp theo
    protected double easeFactor; // độ dễ (giống Anki mặc định là 2.5)
    protected int repetition; // số lần ôn lại liên tiếp
    protected int lapses; // số lần quên
}
