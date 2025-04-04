package mobile.Service;

import mobile.model.Entity.Deck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface DeckService {
    Page<Deck> findByUserId(String userId, Pageable pageable);
    Deck findById(String id);
    Deck save(Deck deck);
    void deleteById(String id);
}
