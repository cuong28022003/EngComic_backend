package mobile.databases.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String resourceType = "raw";
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";

        if (contentType.startsWith("video") || filename.endsWith(".mp4") || filename.endsWith(".mkv") || filename.endsWith(".avi")) {
            resourceType = "video";
        } else if (contentType.contains("pdf") || filename.endsWith(".pdf") || "toeic_pdfs".equals(folder) || (folder != null && folder.contains("pdf"))) {
            resourceType = "raw";
        } else if (contentType.startsWith("image") || filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png") || filename.endsWith(".webp") || filename.endsWith(".gif")) {
            resourceType = "image";
        } else {
            resourceType = "raw";
        }

        Map<String, Object> params = ObjectUtils.asMap(
                "resource_type", resourceType,
                "folder", folder
        );

        Map<?, ?> uploadResult;
        if (file.getSize() > 20 * 1024 * 1024) {
            uploadResult = cloudinary.uploader().uploadLarge(file.getInputStream(), params);
        } else {
            uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        }

        return uploadResult.get("secure_url").toString();
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        int uploadIndex = fileUrl.indexOf("/upload/");
        if (uploadIndex == -1) {
            throw new IllegalArgumentException("Invalid Cloudinary file URL: missing /upload/");
        }

        String resourceType = "image";
        if (fileUrl.contains("/raw/upload/") || fileUrl.toLowerCase().endsWith(".pdf")) {
            resourceType = "raw";
        } else if (fileUrl.contains("/video/upload/")) {
            resourceType = "video";
        }

        String afterUpload = fileUrl.substring(uploadIndex + "/upload/".length());

        if (afterUpload.startsWith("v") && afterUpload.charAt(1) >= '0' && afterUpload.charAt(1) <= '9') {
            int slashAfterVersion = afterUpload.indexOf("/");
            afterUpload = afterUpload.substring(slashAfterVersion + 1);
        }

        String publicId;
        if ("raw".equals(resourceType)) {
            publicId = afterUpload;
        } else {
            int dotIndex = afterUpload.lastIndexOf(".");
            publicId = (dotIndex != -1) ? afterUpload.substring(0, dotIndex) : afterUpload;
        }

        try {
            Map<?, ?> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
            if (!"ok".equals(deleteResult.get("result"))) {
                log.warn("Cloudinary delete result for {}: {}", publicId, deleteResult.get("result"));
            }
        } catch (Exception e) {
            log.warn("Failed to delete file from Cloudinary (publicId: {}): {}", publicId, e.getMessage());
        }
    }
}
