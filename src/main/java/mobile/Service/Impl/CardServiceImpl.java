package mobile.Service.Impl;

import mobile.Service.CardService;
import mobile.config.SrsAlgorithm;
import mobile.model.Entity.Card;
import mobile.repository.CardRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private SrsAlgorithm srsAlgorithm;

    @Override
    public Page<Card> findByDeckId(ObjectId deckId, Pageable pageable) {
        return cardRepository.findByDeckId(deckId, pageable);
    }

    @Override
    public Card findById(ObjectId id) {
        return cardRepository.findById(id).orElse(null);
    }

    @Override
    public Card save(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public void deleteById(ObjectId id) {
        cardRepository.deleteById(id);
    }

    @Override
    public Card review(ObjectId cardId, boolean isCorrect, String reviewState) {
        Card card = cardRepository.findById(cardId).orElse(null);
        if (card != null) {
            card = srsAlgorithm.updateCardReview(card, isCorrect, reviewState);
            return cardRepository.save(card);
        }
        return null;
    }

    @Override
    public List<Card> findAllByDeckId(ObjectId deckId) {
        return cardRepository.findByDeckId(deckId);
    }

    @Override
    public void deleteAllByDeckId(ObjectId deckId) {
        cardRepository.deleteAllByDeckId(deckId);
    }
}
