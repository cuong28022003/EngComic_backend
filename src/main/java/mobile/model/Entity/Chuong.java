package mobile.model.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "chuong")
public class Chuong {

    @Id
    private String id;
    private String tieuDe; // Tiêu đề của chương
    private List<String> danhSachAnh; // Danh sách URL ảnh

    // Constructor mặc định
    public Chuong() {
    }

    // Constructor có tham số
    public Chuong(String id, String tieuDe, List<String> danhSachAnh) {
        this.id = id;
        this.tieuDe = tieuDe;
        this.danhSachAnh = danhSachAnh;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public List<String> getDanhSachAnh() {
        return danhSachAnh;
    }

    public void setDanhSachAnh(List<String> danhSachAnh) {
        this.danhSachAnh = danhSachAnh;
    }
}
