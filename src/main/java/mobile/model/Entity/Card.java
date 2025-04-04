package mobile.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "card")
public class Card {
    @Id
    protected String id;
    protected String deckId;
    protected String front;
    protected String back;
    protected String IPA;
    protected String image;
    protected String audio;
    protected List<String> tags;
    protected Date createAt;
    protected Date updateAt;
}
