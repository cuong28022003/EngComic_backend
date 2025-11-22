package mobile.Service;

import mobile.model.payload.response.translator.TranslatorResponse;
import org.springframework.web.multipart.MultipartFile;

public interface TranslatorService {
    TranslatorResponse getIpaAndMeaning(String text);
    TranslatorResponse extractTextFromImage(MultipartFile image);
}
