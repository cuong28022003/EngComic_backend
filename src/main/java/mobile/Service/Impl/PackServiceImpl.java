package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.PackService;
import mobile.model.Entity.Pack;
import mobile.repository.PackRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PackServiceImpl implements PackService {
    private final PackRepository packRepository;

    public Pack getPackById(ObjectId id) {
        return packRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pack not found"));
    }
}
