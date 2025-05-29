package mobile.model.payload.response.deck;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeckResponse {
    protected String id;
    protected String name;
    protected String description;
    protected Date createAt;
    protected Date updateAt;
    protected DeckStatisticsResponse stats;
}
