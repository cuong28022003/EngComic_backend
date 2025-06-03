package mobile.Service.Impl;

import javax.transaction.Transactional;

import mobile.mapping.ReadingMapping;
import mobile.model.payload.response.reading.ReadingResponse;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.Service.ReadingService;
import mobile.model.Entity.Reading;
import mobile.repository.ReadingRepository;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReadingServiceImpl implements ReadingService {
    final ReadingRepository readingRepository;
    private final ReadingMapping readingMapping;

    @Override
    public Page<ReadingResponse> getReadingsByUserId(ObjectId userId, Pageable pageable) {
        Page<Reading> readings = readingRepository.findByUserId(userId, pageable);
        if (readings.isEmpty()) {
            throw new IllegalArgumentException("No readings found for user with id: " + userId);
        }
        return readings.map(reading -> {
            return readingMapping.toReadingResponse(reading);
        });
    }

    @Override
    public void deleteAllReadingByComicId(ObjectId comicId) {
        if (comicId == null) {
            throw new IllegalArgumentException("Comic ID cannot be null");
        }
        readingRepository.deleteAllByComicId(comicId);
        log.info("Deleted all readings for comic with id: {}", comicId);
    }

@Override
        public ReadingResponse findByUserIdAndComicId(ObjectId userId, ObjectId comicId) {
            if (userId == null || comicId == null) {
                throw new IllegalArgumentException("User ID and Comic ID cannot be null");
            }
            Reading reading = readingRepository.findByUserIdAndComicId(userId, comicId)
                    .orElseGet(() -> {
                        Reading newReading = new Reading();
                        newReading.setUserId(userId);
                        newReading.setComicId(comicId);
                        newReading.setChapterNumber(1);
                        return readingRepository.save(newReading);
                    });
            return readingMapping.toReadingResponse(reading);
        }

    @Override
    public ReadingResponse createReading(ObjectId userId, ObjectId comicId, int chapterNumber) {
        if (userId == null || comicId == null || chapterNumber <= 0) {
            throw new IllegalArgumentException("User ID, Comic ID, and ChapterNumber cannot be null");
        }
        Reading reading = readingRepository.findByUserIdAndComicId(userId, comicId)
                .orElseGet(() -> {
                    Reading newReading = new Reading();
                    newReading.setUserId(userId);
                    newReading.setComicId(comicId);
                    newReading.setChapterNumber(chapterNumber);
                    return readingRepository.save(newReading);
                });
        reading.setChapterNumber(chapterNumber);
        Reading savedReading = readingRepository.save(reading);
        return readingMapping.toReadingResponse(savedReading);
    }
}
