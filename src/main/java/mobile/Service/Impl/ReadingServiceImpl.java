package mobile.Service.Impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.Service.ReadingService;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Reading;
import mobile.model.Entity.User;
import mobile.repository.ReadingRepository;


@Service @RequiredArgsConstructor @Transactional @Slf4j
public class ReadingServiceImpl implements ReadingService{
	final ReadingRepository readingRepository;

	@Override
	public void saveReading(Reading reading) {
		Optional<Reading> readingDB = readingRepository.findWithParam(reading.getUser().getId(), reading.getComic().getId());
		if(readingDB.isEmpty()) {
			Reading newReading = new Reading(reading.getUser(),reading.getChapterNumber(),reading.getComic());
			readingRepository.save(newReading);
		} else {
			Reading oldReading = readingDB.get();
			oldReading.setChapterNumber(reading.getChapterNumber());
			readingRepository.save(oldReading);
		}
	}

	@Override
	public Page<Reading> getReadings(User user, Pageable pageable) {
		return readingRepository.findByUser(user, pageable);
	}

	@Override
	public void deleteAllReadingByComic(Comic comic) {
		readingRepository.deleteAllByComic(comic);
	}

	@Override
	public Optional<Reading> getReading(User user, Comic comic) {
		return readingRepository.findWithParam(user.getId(), comic.getId());
	}

	@Override
	public Reading save(Reading reading) {
		return readingRepository.save(reading);
	}

	@Override
	public Reading findByComicAndUser(Comic comic, User user) {
		return readingRepository.findByComicAndUser(comic, user);
	}
}
