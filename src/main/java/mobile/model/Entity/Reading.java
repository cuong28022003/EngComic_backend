package mobile.model.Entity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported=false)
@Document(collection = "reading")
public class Reading {
    @Id
    protected ObjectId _id;
	@DBRef
	protected User user;
    protected int chapterNumber;
	@DBRef
    protected Comic comic;

	public Reading() {
	}

	public Reading(ObjectId _id, User user, int chapterNumber, Comic comic) {
		this._id = _id;
		this.user = user;
		this.chapterNumber = chapterNumber;
		this.comic = comic;
	}

	public Reading(User user, int chapterNumber, Comic comic) {
		this.user = user;
		this.chapterNumber = chapterNumber;
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

	public int getChapterNumber() {
		return chapterNumber;
	}

	public void setChapterNumber(int chapterNumber) {
		this.chapterNumber = chapterNumber;
	}

	public Comic getComic() {
		return comic;
	}

	public void setComic(Comic comic) {
		this.comic = comic;
	}
}