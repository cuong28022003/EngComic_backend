package mobile.model.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.payload.response.comic.ComicResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SavedResponse {
    private String id;
    private String userId;
    private ComicResponse comic;
    private LocalDateTime createdAt;
}
