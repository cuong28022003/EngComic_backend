package mobile.apis.comic.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedResponseDto {
    private String id;
    private String userId;
    private String comicId;
    private LocalDateTime createdAt;
    private ComicResponseDto comic;
}

