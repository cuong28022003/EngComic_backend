package mobile.model.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadingResponse {
    protected String name;
    protected String image;
    protected int chapterNumber;
    protected int chapterCount;
    protected String url;
}
