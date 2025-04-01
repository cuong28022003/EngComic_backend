package mobile.model.payload.request.chapter;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateChapterRequest {
    protected String name;
    protected MultipartFile[] images; // Danh sách URL ảnh
    protected String url;
}
