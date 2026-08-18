package mobile.apis.gacha.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackResponseDto {
    private String id;
    private String name;
    private String imageUrl;
    private String description;
}
