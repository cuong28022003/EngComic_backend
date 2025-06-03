package mobile.model.payload.response.chapter;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterResponse {
    private String id;
    private int chapterNumber;
    private String name;
    private String imageUrl;
    private String comicId;
    private List<Map<Integer,String>> pageUrls; // Danh sách URL ảnh
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
