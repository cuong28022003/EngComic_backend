package mobile.searchcriteria.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSearchCriteria {
    private String userId;
    private String deckId;
    private String search;
    private String status;
    private String topic;
    private Pageable pageable;
}
