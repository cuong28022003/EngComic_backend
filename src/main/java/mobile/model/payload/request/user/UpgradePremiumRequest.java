package mobile.model.payload.request.user;

import lombok.Data;

@Data
public class UpgradePremiumRequest {
    private String userId;
    private int days;
    private int cost;
}
