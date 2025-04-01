package mobile.Service;

import mobile.model.Entity.Comic;
import mobile.model.Entity.User;

import mobile.model.Entity.Reading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface ReadingService {
   void saveReading(Reading reading);
   Page<Reading> getReadings(User user, Pageable pageable);
   void deleteAllReadingByComic(Comic comic);
   Optional<Reading> getReading(User user, Comic comic);
   Reading save(Reading reading);

   Reading findByComicAndUser(Comic comic, User user);
}
