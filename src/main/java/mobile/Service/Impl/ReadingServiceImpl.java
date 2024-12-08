package mobile.Service.Impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

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
	public void upsertReading(Reading reading) {
		Optional<Reading> readingDB = readingRepository.findWithParam(reading.getUser().getId(), reading.getNovel().getId());
		if(readingDB.isEmpty()) {
			Reading newReading = new Reading(reading.getUser(),reading.getChapnumber(),reading.getNovel());
			readingRepository.save(newReading);
		} else {
			Reading oldReading = readingDB.get();
			oldReading.setChapnumber(reading.getChapnumber());
			readingRepository.save(oldReading);
		}
	}

	@Override
	public List<Reading> getReadings(User user) {
		return readingRepository.findByUserId(user.getId());
	}

	@Override
	public void deleteAllReadingByNovel(Comic comic) {
		readingRepository.deleteAllByNovel(comic);
	}

	@Override
	public Optional<Reading> getReading(User user, Comic comic) {
		return readingRepository.findWithParam(user.getId(), comic.getId());
	}

	@Override
	public Reading save(Reading reading) {
		return readingRepository.save(reading);
	}
}
