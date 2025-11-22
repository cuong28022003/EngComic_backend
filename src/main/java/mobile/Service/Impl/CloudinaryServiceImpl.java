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
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String resourceType = file.getContentType() != null && file.getContentType().startsWith("video")
                ? "video"
                : "image"; // Default to image if not a video

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", resourceType,
                "folder", folder // chỉ định thư mục
        ));
        return uploadResult.get("secure_url").toString();
    }


    @Override
    public void deleteFile(String fileUrl) throws IOException {
        // Bước 1: tìm vị trí "/upload/"
        int uploadIndex = fileUrl.indexOf("/upload/");
        if (uploadIndex == -1) {
            throw new IllegalArgumentException("Invalid Cloudinary file URL: missing /upload/");
        }

        // Bước 2: cắt sau "/upload/"
        String afterUpload = fileUrl.substring(uploadIndex + "/upload/".length());

        // Bước 3: nếu có version (bắt đầu bằng "v" và theo sau là số), loại bỏ
        if (afterUpload.startsWith("v") && afterUpload.charAt(1) >= '0' && afterUpload.charAt(1) <= '9') {
            int slashAfterVersion = afterUpload.indexOf("/");
            afterUpload = afterUpload.substring(slashAfterVersion + 1); // bỏ phần v1234567890/
        }

        // Bước 4: loại bỏ phần mở rộng (.jpg, .png...)
        int dotIndex = afterUpload.lastIndexOf(".");
        String publicId = (dotIndex != -1) ? afterUpload.substring(0, dotIndex) : afterUpload;

        // Gọi Cloudinary xoá file
        Map<?, ?> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        if (!"ok".equals(deleteResult.get("result"))) {
            throw new RuntimeException("Failed to delete file from Cloudinary");
        }
    }


}
