package mobile.Service.Impl;

import mobile.Service.CardService;
import mobile.model.Entity.Card;
import mobile.repository.CardRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

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
}
