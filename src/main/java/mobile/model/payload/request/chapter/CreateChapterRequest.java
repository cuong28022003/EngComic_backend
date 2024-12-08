package mobile.model.payload.request.chapter;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateChapterRequest {
    protected String tenchap;
    protected List<String> danhSachAnh; // Danh sách URL ảnh
    protected String url;
}
