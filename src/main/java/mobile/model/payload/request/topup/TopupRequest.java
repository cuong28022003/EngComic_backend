package mobile.model.payload.request.topup;

import lombok.Data;

@Data
public class TopupRequest {
    private String userId;
    private int diamond;
    private String note;
}
