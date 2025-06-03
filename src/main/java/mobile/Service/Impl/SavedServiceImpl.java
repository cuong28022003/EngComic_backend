package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.Service.SavedService;
import mobile.mapping.SavedMapping;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Saved;
import mobile.model.payload.response.SavedResponse;
import mobile.repository.SavedRepository;
import mobile.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SavedServiceImpl implements SavedService {
    final SavedRepository savedRepository;
    private final SavedMapping savedMapping;

    @Override
    public Page<SavedResponse> getByUserId(ObjectId userId, Pageable pageable) {
      Page<Saved> saveds = savedRepository.findByUserId(userId, pageable);
        return saveds.map(saved -> savedMapping.toSavedResponse(saved));
    }

    @Override
    public SavedResponse create(ObjectId userId, ObjectId comicId) {
        Saved saved = new Saved(userId, comicId);
        savedRepository.save(saved);
        return savedMapping.toSavedResponse(saved);
    }

    @Override
    public void delete(ObjectId id) {
        Optional<Saved> saved = savedRepository.findById(id);
        if (saved.isPresent()) {
            savedRepository.delete(saved.get());
        } else {
            log.warn("Saved with id {} not found", id);
        }
    }

    @Override
    public SavedResponse getById(ObjectId id) {
        Optional<Saved> saved = savedRepository.findById(id);
        if (saved.isPresent()) {
            return savedMapping.toSavedResponse(saved.get());
        } else {
            log.warn("Saved with id {} not found", id);
            return null; // or throw an exception
        }
    }

    @Override
    public SavedResponse getByUserIdAndComicId(ObjectId userId, ObjectId comicId) {
        Optional<Saved> saved = savedRepository.findByUserIdAndComicId(userId, comicId);
        if (saved.isPresent()) {
            return savedMapping.toSavedResponse(saved.get());
        } else {
            log.warn("Saved with userId {} and comicId {} not found", userId, comicId);
            return null; // or throw an exception
        }
    }

    @Override
    public void deleteAllByComicId(ObjectId comicId) {
        List<Saved> savedList = savedRepository.findAllByComicId(comicId);
        if (!savedList.isEmpty()) {
            savedRepository.deleteAll(savedList);
            log.info("Deleted all saved entries for comicId: {}", comicId);
        } else {
            log.warn("No saved entries found for comicId: {}", comicId);
        }
    }
}
