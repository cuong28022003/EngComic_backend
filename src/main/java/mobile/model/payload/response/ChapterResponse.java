package mobile.model.payload.response;

import com.mongodb.DBRef;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.model.Entity.Comic;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChapterResponse {
    private int chapterNumber;
    private Comic comic;
    private String name;
    private List<String> images;
    private Date createAt;
    private Date updateAt;
    private int totalChapters;
}
