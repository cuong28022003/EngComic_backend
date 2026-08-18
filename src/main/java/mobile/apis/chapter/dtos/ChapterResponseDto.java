package mobile.apis.chapter.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponseDto {
    private String id;
    private int chapterNumber;
    private String name;
    private String imageUrl;
    private String comicId;
    private List<Map<Integer, String>> pageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
