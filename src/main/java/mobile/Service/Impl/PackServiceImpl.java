package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.CloudinaryService;
import mobile.Service.PackService;
import mobile.model.Entity.Pack;
import mobile.repository.PackRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackServiceImpl implements PackService {
    private final PackRepository packRepository;
    private final CloudinaryService cloudinaryService;

    public Pack getPackById(ObjectId id) {
        return packRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pack not found"));
    }

    @Override
    public Pack createPack(String name, String description, MultipartFile image) {
        Pack pack = new Pack();
        pack.setName(name);
        pack.setDescription(description);
        try {
            String imageUrl = cloudinaryService.uploadFile(image);
            pack.setImageUrl(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }
        return packRepository.save(pack);
    }

    @Override
    public List<Pack> getAllPacks() {
        return packRepository.findAll();
    }

    @Override
    public Pack updatePack(ObjectId id, String name, String description, MultipartFile image) {
        Pack existingPack = getPackById(id);
        existingPack.setName(name);
        existingPack.setDescription(description);
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadFile(image);
                existingPack.setImageUrl(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        return packRepository.save(existingPack);
    }

    @Override
    public void deletePack(ObjectId id) {
        packRepository.deleteById(id);
    }
}
