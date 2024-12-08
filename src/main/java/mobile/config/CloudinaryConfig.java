package mobile.config;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "drrfpqhtl",
            "api_key", "197247714914274",
            "api_secret", "Js6a9tuK3YBXtYhEBHmZ_4OXyYI"
        ));
    }
}
