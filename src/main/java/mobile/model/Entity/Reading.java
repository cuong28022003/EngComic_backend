package mobile.model.Entity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported=false)
@Document(collection = "readings")
public class Reading {
    @Id
    protected ObjectId _id;
	@DBRef
	protected User user;
    protected int chapnumber;
	@DBRef
    protected Comic comic;
    
    public Reading() {
    	
    }
    
	public Reading( User user, int chapnumber, Comic comic) {
		this._id = new ObjectId();
		this.user=user;
		this.chapnumber = chapnumber;
		this.comic = comic;
	}

	public Reading(ObjectId _id, User user, int chapnumber, Comic comic) {
		this._id = _id;
		this.user=user;
		this.chapnumber = chapnumber;
		this.comic = comic;
	}

	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public int getChapnumber() {
		return chapnumber;
	}
	public void setChapnumber(int chapnumber) {
		this.chapnumber = chapnumber;
	}
	public Comic getNovel() {
		return comic;
	}
	public void setNovel(Comic comic) {
		this.comic = comic;
	}
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
}