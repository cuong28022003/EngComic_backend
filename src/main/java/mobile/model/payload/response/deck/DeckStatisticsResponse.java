package mobile.model.payload.response.deck;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeckStatisticsResponse {
    private long totalNew;
    private long totalEasy;
    private long totalHard;
    private long totalDue;
}
