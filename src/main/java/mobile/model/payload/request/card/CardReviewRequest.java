package mobile.model.payload.request.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardReviewRequest {
    private String cardId;
    private String userAnswer;
    private boolean isCorrect;
    private String reviewState; // AGAIN | HARD | GOOD | EASY
}
