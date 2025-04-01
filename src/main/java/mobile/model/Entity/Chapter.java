package mobile.model.Entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Date;
import java.util.List;

@RestResource(exported = false)
@Document(collection = "chapter")
public class Chapter {
    @Id
    protected String id;
    protected int chapterNumber;
    @DBRef
    protected Comic comic;
    protected String name;
    protected List<String> images; // Danh sách URL ảnh
    @CreatedDate
    protected Date createAt;
    @LastModifiedDate
    protected Date updateAt;

    public Chapter() {
    }

    public Chapter(String id, int chapterNumber, Comic comic, String name, List<String> images, Date createAt, Date updateAt) {
        this.id = id;
        this.chapterNumber = chapterNumber;
        this.comic = comic;
        this.name = name;
        this.images = images;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}

