package mobile.Service;

import mobile.model.Entity.Comic;
import mobile.model.Entity.User;

import mobile.model.Entity.Reading;

import java.util.List;
import java.util.Optional;


public interface ReadingService {
   void upsertReading(Reading reading);
   List<Reading> getReadings(User user);
   void deleteAllReadingByNovel(Comic comic);
   Optional<Reading> getReading(User user, Comic comic);
   Reading save(Reading reading);
}
