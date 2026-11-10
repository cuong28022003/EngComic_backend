package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.businesses.boundaries.user.UpdateUserProfile;
import mobile.databases.entities.user.UserEntity;
import mobile.databases.repositories.user.UserRepository;
import mobile.databases.services.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateUserProfileInteractor implements UpdateUserProfile {

    private static final String AVATAR_FOLDER = "user_avatars";
    private static final String COVER_FOLDER = "user_covers";

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final UserMapper userMapper;

    @Override
    public Response execute(Request request) {
        if (request.getCurrentUserId() == null || request.getCurrentUserId().isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getTargetUserId() == null || request.getTargetUserId().isBlank()) {
            throw new IllegalArgumentException("Target user ID is required");
        }
        // Chỉ cho phép cập nhật hồ sơ của chính mình
        if (!request.getCurrentUserId().equals(request.getTargetUserId())) {
            throw new IllegalArgumentException("Bạn không có quyền cập nhật hồ sơ của người khác");
        }

        UserEntity user = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getTargetUserId()));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }

        // Nhân vật nổi bật hiển thị trên hồ sơ (tối đa 3)
        if (request.getFeaturedCharacterIds() != null) {
            List<String> ids = request.getFeaturedCharacterIds().stream()
                    .filter(id -> id != null && !id.isBlank())
                    .distinct()
                    .limit(3)
                    .collect(Collectors.toList());
            user.setFeaturedCharacterIds(ids);
        }

        // Ảnh đại diện: ưu tiên file upload, sau đó tới URL có sẵn
        if (hasFile(request.getImage())) {
            String oldImage = user.getImage();
            user.setImage(upload(request.getImage(), AVATAR_FOLDER));
            deleteIfCloudinary(oldImage, user.getImage());
        } else if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            user.setImage(request.getImageUrl());
        }

        // Ảnh bìa: ưu tiên file upload, sau đó tới URL có sẵn
        if (hasFile(request.getCover())) {
            String oldCover = user.getCover();
            user.setCover(upload(request.getCover(), COVER_FOLDER));
            deleteIfCloudinary(oldCover, user.getCover());
        } else if (request.getCoverUrl() != null && !request.getCoverUrl().isBlank()) {
            user.setCover(request.getCoverUrl());
        }

        UserEntity saved = userRepository.save(user);
        return Response.builder()
                .profile(userMapper.toProfileDto(saved))
                .build();
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private String upload(MultipartFile file, String folder) {
        try {
            return cloudinaryService.uploadFile(file, folder);
        } catch (IOException e) {
            throw new RuntimeException("Tải ảnh lên thất bại: " + e.getMessage(), e);
        }
    }

    private void deleteIfCloudinary(String oldUrl, String newUrl) {
        if (oldUrl == null || oldUrl.isBlank() || oldUrl.equals(newUrl)) {
            return;
        }
        if (oldUrl.contains("res.cloudinary.com")) {
            try {
                cloudinaryService.deleteFile(oldUrl);
            } catch (IOException ignored) {
            }
        }
    }
}
