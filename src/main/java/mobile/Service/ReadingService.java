package mobile.Service;

import mobile.model.Entity.Comic;

import mobile.model.payload.response.reading.ReadingResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReadingService {
   Page<ReadingResponse> getReadingsByUserId(ObjectId userId, Pageable pageable);
   void deleteAllReadingByComicId(ObjectId comicId);

   ReadingResponse findByUserIdAndComicId(ObjectId userId, ObjectId comicId);

   ReadingResponse createReading(ObjectId userId, ObjectId comicId, int chapterNumber);
}
