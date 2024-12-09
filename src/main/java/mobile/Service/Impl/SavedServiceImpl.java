package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.Service.SavedService;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Saved;
import mobile.repository.SavedRepository;
import mobile.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SavedServiceImpl implements SavedService {
    final SavedRepository savedRepository;
    final UserRepository userRepository;
    @Override
    public List<Saved> getSavedByUserId(ObjectId id) {
        return savedRepository.findByUserId(id);
    }
    @Override
    public Saved createSaved(Saved saved) {
        return savedRepository.save(saved);
    }
    @Override
    public Saved deleteSaved(ObjectId userId, ObjectId comicId) {
        return savedRepository.deleteByParam(userId, comicId);
    }
    @Override
    public Saved getSaved(ObjectId userId, ObjectId comicId) {
        return savedRepository.findByParam(userId, comicId);
    }

    @Override
    public void DeleteSavedByComic(Comic findComic) {
        savedRepository.deleteByComic(findComic);
    }
}
