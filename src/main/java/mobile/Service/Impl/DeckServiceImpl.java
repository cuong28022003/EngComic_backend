package mobile.Service.Impl;

import mobile.Service.CardService;
import mobile.Service.DeckService;
import mobile.model.Entity.Card;
import mobile.model.Entity.Deck;
import mobile.model.payload.response.deck.DeckStatisticsResponse;
import mobile.repository.DeckRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class DeckServiceImpl implements DeckService {

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private CardService cardService;

    @Override
    public Page<Deck> findByUserId(ObjectId userId, Pageable pageable) {
         return deckRepository.findByUserId(userId, pageable);
    }

    @Override
    public Deck findById(ObjectId id) {
         return deckRepository.findById(id).orElse(null);
    }

    @Override
    public Deck save(Deck deck) {
         return deckRepository.save(deck);
    }

@Override
         public void deleteById(ObjectId id) {
             cardService.deleteAllByDeckId(id); // Xóa tất cả các card thuộc deck
             deckRepository.deleteById(id);    // Xóa deck
         }

    @Override
    public DeckStatisticsResponse getDeckStatistics(ObjectId deckId) {
        List<Card> cards = cardService.findAllByDeckId(deckId);

        long totalCards = cards.size();
        long totalNew = cards.stream().filter(r -> r.getReviewCount() == 0).count();
        long totalEasy = cards.stream().filter(r -> r.getEaseFactor() > 2.5).count();
        long totalHard = cards.stream().filter(r -> r.getEaseFactor() <= 2.0).count();
        long totalDue = cards.stream().filter(r -> r.getNextReview() != null && r.getNextReview().before(new Date())).count();
        return new DeckStatisticsResponse(totalCards, totalNew, totalEasy, totalHard, totalDue);
    }
}
