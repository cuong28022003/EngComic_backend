package mobile.apis.comment.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.user.dtos.UserProfileDto;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private String id;
    private String parentId;
    private List<CommentResponseDto> listChild;
    private String comicUrl;
    private UserProfileDto user;
    private int numChild;
    private int depth;
    private String content;
    private Date createDate;
}
