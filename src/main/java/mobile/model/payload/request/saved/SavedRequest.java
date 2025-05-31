package mobile.model.payload.request.saved;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SavedRequest {
    private String userId;
    private String comicId;
}

