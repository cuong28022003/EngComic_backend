package mobile.apis.comic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;
import mobile.apis.user.dtos.UserProfileDto;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {
    private String id;
    private String comicId;
    private String userId;
    private String reason;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private ComicResponseDto comic;
    private UserProfileDto user;
}

