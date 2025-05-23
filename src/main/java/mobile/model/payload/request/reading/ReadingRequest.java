package mobile.model.payload.request.reading;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReadingRequest {
    protected int chapterNumber;
    protected String url;

}
