package mobile.apis.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupResponseDto {
    private String id;
    private String userId;
    private int diamond;
    private String note;
    private LocalDateTime createdAt;
    private boolean processed;
    private boolean canceled;
}

