package mobile.model.payload.response.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardReviewResponse {
    private String cardId;
    private boolean isCorrect;
    private String userAnswer;
    private String reviewState; // AGAIN | HARD | GOOD | EASY
    private Date lastReviewed;
    private Date nextReview;
    private Integer interval;        // số ngày sẽ ôn lại
    private Double easeFactor;       // độ dễ của thẻ
    private Integer repetition;      // số lần trả lời đúng liên tiếp
    private Integer lapses;          // số lần quên
}
