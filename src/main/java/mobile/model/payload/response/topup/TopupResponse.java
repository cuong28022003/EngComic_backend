package mobile.model.payload.response.topup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopupResponse {
    String id;
    String userId;
    int diamond;
    String note;
    LocalDateTime createdAt;
    boolean processed;
    boolean canceled = false;
}
