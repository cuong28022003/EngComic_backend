package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CardReviewService;
import mobile.config.SrsAlgorithm;
import mobile.model.Entity.CardReview;
import mobile.repository.CardReviewRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CardReviewServiceImpl implements CardReviewService {

    private final CardReviewRepository cardReviewRepository;
    private final SrsAlgorithm srsAlgorithm;

    @Override
    public List<CardReview> getDueCardReviews(ObjectId userId) {
        return cardReviewRepository.findByUserIdAndNextReviewBefore(userId, new Date());
    }

    @Override
    public CardReview updateReview(ObjectId userId, ObjectId cardId, boolean isCorrect) {
        CardReview review = cardReviewRepository.findByUserIdAndCardId(userId, cardId)
                .orElse(srsAlgorithm.initNewReview(userId, cardId));

        review = srsAlgorithm.calculate(review, isCorrect);

        return cardReviewRepository.save(review);
    }
}
