package mobile.model.Entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported=false)
@Document(collection = "saved")
public class Saved {
    @Id
    protected ObjectId _id;
    @DBRef
    protected User user;
    @DBRef
    protected Comic comic;
    public ObjectId getId() {return _id;}
    public void setId(ObjectId _id) {this._id = _id;}
    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
    public Comic getComic() {return comic;}
    public void setComic(Comic comic) {this.comic = comic;}
    public Saved() {
    }
    public  Saved(ObjectId id, User user, Comic comic){
        this._id = id;
        this.user=user;
        this.comic = comic;
    }
    public Saved(User user, Comic comic){
        this._id = new ObjectId();
        this.user=user;
        this.comic = comic;
    }

}
