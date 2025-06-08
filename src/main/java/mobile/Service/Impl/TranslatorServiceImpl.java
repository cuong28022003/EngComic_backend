package mobile.Service.Impl;

import mobile.Service.TranslatorService;
import mobile.model.payload.response.translator.TranslatorResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TranslatorServiceImpl implements TranslatorService {
    private final RestTemplate restTemplate = new RestTemplate();
//    private final String PYTHON_API_URL = "http://localhost:5000/api/ipa-meaning";
    private final String PYTHON_API_URL = "https://web-production-b5c49.up.railway.app/api/ipa-meaning";

    @Override
    public TranslatorResponse getIpaAndMeaning(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("text", text), headers);

        try {
            ResponseEntity<TranslatorResponse> response = restTemplate.exchange(
                    PYTHON_API_URL,
                    HttpMethod.POST,
                    request,
                    TranslatorResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            return new TranslatorResponse("", "Không thể gọi API Python: " + e.getMessage());
        }
    }
}
