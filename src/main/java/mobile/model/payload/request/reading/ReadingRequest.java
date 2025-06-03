package mobile.model.payload.request.reading;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReadingRequest {
    protected String userId;
    protected String comicId;
    protected int chapterNumber;
}
