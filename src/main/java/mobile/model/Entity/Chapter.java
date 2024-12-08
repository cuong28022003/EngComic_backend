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
@Document(collection = "chapters")
public class Chapter {
    @Id
    protected ObjectId _id;
    protected int chapnumber;
    @DBRef
    protected Comic dautruyenId;
    protected String tenchap;
    protected List<String> danhSachAnh; // Danh sách URL ảnh
    @CreatedDate
    protected Date createAt;
    @LastModifiedDate
    protected Date updateAt;

    // Constructor mặc định
    public Chapter() {
    }

    // Constructor có tham số
    public Chapter(ObjectId _id, int chapnumber, Comic dautruyenId, String tenchap,
            List<String> danhSachAnh, Date createAt, Date updateAt) {
        this._id = _id;
        this.chapnumber = chapnumber;
        this.dautruyenId = dautruyenId;
        this.tenchap = tenchap;
        this.danhSachAnh = danhSachAnh;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    // Getters và Setters
    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public int getChapnumber() {
        return chapnumber;
    }

    public void setChapnumber(int chapnumber) {
        this.chapnumber = chapnumber;
    }

    public Comic getDautruyenId() {
        return dautruyenId;
    }

    public void setDautruyenId(Comic dautruyenId) {
        this.dautruyenId = dautruyenId;
    }

    public String getTenchap() {
        return tenchap;
    }

    public void setTenchap(String tenchap) {
        this.tenchap = tenchap;
    }

    public List<String> getDanhSachAnh() {
        return danhSachAnh;
    }

    public void setDanhSachAnh(List<String> danhSachAnh) {
        this.danhSachAnh = danhSachAnh;
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
