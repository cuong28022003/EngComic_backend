package mobile.model.payload.request.comment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentRequest {
    protected String url;
    protected String parentId;
    protected String content;
}
