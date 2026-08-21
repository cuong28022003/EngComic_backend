package mobile.databases.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String resourceType = "image";
        if (file.getContentType() != null) {
            if (file.getContentType().startsWith("video")) {
                resourceType = "video";
            } else if (file.getContentType().equals("application/pdf") ||
                    (file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().endsWith(".pdf"))) {
                resourceType = "raw";
            }
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", resourceType,
                "folder", folder
        ));
        return uploadResult.get("secure_url").toString();
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        int uploadIndex = fileUrl.indexOf("/upload/");
        if (uploadIndex == -1) {
            throw new IllegalArgumentException("Invalid Cloudinary file URL: missing /upload/");
        }

        String afterUpload = fileUrl.substring(uploadIndex + "/upload/".length());

        if (afterUpload.startsWith("v") && afterUpload.charAt(1) >= '0' && afterUpload.charAt(1) <= '9') {
            int slashAfterVersion = afterUpload.indexOf("/");
            afterUpload = afterUpload.substring(slashAfterVersion + 1);
        }

        int dotIndex = afterUpload.lastIndexOf(".");
        String publicId = (dotIndex != -1) ? afterUpload.substring(0, dotIndex) : afterUpload;

        Map<?, ?> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        if (!"ok".equals(deleteResult.get("result"))) {
            throw new RuntimeException("Failed to delete file from Cloudinary");
        }
    }
}
