package mobile.model.payload.response.reading;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.model.payload.response.comic.ComicResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingResponse {
    private String id;
    private String userId;
    private ComicResponse comic;
    private int chapterNumber;
}
