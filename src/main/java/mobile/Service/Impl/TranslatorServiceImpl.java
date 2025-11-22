package mobile.Service.Impl;

import mobile.Service.TranslatorService;
import mobile.model.payload.response.translator.TranslatorResponse;
import mobile.utils.MultipartInputStreamFileResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class TranslatorServiceImpl implements TranslatorService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String PYTHON_API_URL = "http://localhost:8000";
//    private final String PYTHON_API_URL = "https://web-production-b5c49.up.railway.app/api/ipa-meaning";

    @Override
    public TranslatorResponse getIpaAndMeaning(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("text", text), headers);

        try {
            ResponseEntity<TranslatorResponse> response = restTemplate.exchange(
                    PYTHON_API_URL + "/api/ipa-meaning",
                    HttpMethod.POST,
                    request,
                    TranslatorResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            return new TranslatorResponse("", "Không thể gọi API Python: " + e.getMessage(), "");
        }
    }

    @Override
    public TranslatorResponse extractTextFromImage(MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultipartInputStreamFileResource fileResource =
                    new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<TranslatorResponse> response = restTemplate.exchange(
                    PYTHON_API_URL + "/api/gemini/extract",
                    HttpMethod.POST,
                    requestEntity,
                    TranslatorResponse.class
            );

            return response.getBody();
        } catch (IOException e) {
            return new TranslatorResponse("", "Không thể đọc file: " + e.getMessage(), "");
        } catch (Exception e) {
            return new TranslatorResponse("", "Không thể gọi API Python: " + e.getMessage(), "");
        }
    }
}
