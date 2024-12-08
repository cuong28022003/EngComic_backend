package mobile.model.Entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Entity;

@RestResource(exported=false)
@Document(collection = "ratings")
public class Rating {
    @Id
    protected ObjectId _id;
    @DBRef
    protected User user;
    protected int rating;
    @DBRef
    protected Comic comic;

    public Rating() {
    }

    public Rating(ObjectId _id, User user, int rating, Comic comic) {
        this._id = _id;
        this.user = user;
        this.rating = rating;
        this.comic = comic;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Comic getComic() {
        return comic;
    }

    public void setComic(Comic comic) {
        this.comic = comic;
    }
}
