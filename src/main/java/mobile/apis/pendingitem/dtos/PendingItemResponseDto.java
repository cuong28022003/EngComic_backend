package mobile.apis.pendingitem.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PendingItemResponseDto {
    private String id;
    private String userId;
    private String content;
    private String sourceType;
    private String sourceCardId;
    private String status;
    private String createdAt;
}
