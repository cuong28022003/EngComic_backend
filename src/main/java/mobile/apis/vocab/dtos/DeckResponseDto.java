package mobile.apis.vocab.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeckResponseDto {
    private String id;
    private String userId;
    private String name;
    private String description;
    private DeckStatisticsResponse stats;
    private String createAt;
    private String updateAt;
}

