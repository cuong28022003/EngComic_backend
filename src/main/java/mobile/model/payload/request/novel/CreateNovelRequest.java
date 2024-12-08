package mobile.model.payload.request.novel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data

public class CreateNovelRequest {
    protected String name;
    protected String genre;
    protected String artist;
    protected String url;
    protected String image;
    protected String description;
}
