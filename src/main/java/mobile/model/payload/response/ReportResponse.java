package mobile.model.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponse {
    private String id;
    private String comicId;
    private String userId;
    private String comicTitle;
    private String username;
    private String reason;
    private String status;
    private String createdAt;
    private String updatedAt;
}