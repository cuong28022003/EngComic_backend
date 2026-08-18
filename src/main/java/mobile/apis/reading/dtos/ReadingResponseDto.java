package mobile.apis.reading.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingResponseDto {
    private String id;
    private String userId;
    private String comicId;
    private int chapterNumber;
    private ComicResponseDto comic;
}
