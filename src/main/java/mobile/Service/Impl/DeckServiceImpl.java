package mobile.Service.Impl;

import mobile.Service.DeckService;
import mobile.model.Entity.Deck;
import mobile.repository.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeckServiceImpl implements DeckService {

    @Autowired
    private DeckRepository deckRepository;

    @Override
    public Page<Deck> findByUserId(String userId,Pageable pageable) {
         return deckRepository.findByUserId(userId, pageable);
    }

    @Override
    public Deck findById(String id) {
         return deckRepository.findById(id).orElse(null);
    }

    @Override
    public Deck save(Deck deck) {
         return deckRepository.save(deck);
    }

    @Override
    public void deleteById(String id) {
         deckRepository.deleteById(id);
    }
}
