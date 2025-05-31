package mobile.model.Entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RestResource(exported = false)
@Document(collection = "topup")
public class Topup {
    @Id
    private ObjectId id;

    private ObjectId userId;
    private int diamond;
    private String note; // Nội dung chuyển khoản người dùng đã ghi (VD: nap500_user123)
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo yêu cầu nạp tiền
    private boolean processed; // Đã xác minh và cộng kim cương hay chưa
    private boolean canceled = false;
}
