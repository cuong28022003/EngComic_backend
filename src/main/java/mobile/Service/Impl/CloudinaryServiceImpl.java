package mobile.Service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import mobile.Service.CloudinaryService;
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
    public String uploadFile(MultipartFile file) throws IOException {
        String resourceType = file.getContentType() != null && file.getContentType().startsWith("video")
                ? "video"
                : "image"; // Default to image if not a video

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", resourceType
        ));
        return uploadResult.get("secure_url").toString(); // Return the secure URL of the uploaded file
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        // Extract public_id from the file URL
        String publicId = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.lastIndexOf("."));

        // Call Cloudinary API to delete the file
        Map<?, ?> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        if (!"ok".equals(deleteResult.get("result"))) {
            throw new RuntimeException("Failed to delete file from Cloudinary");
        }
    }
}
