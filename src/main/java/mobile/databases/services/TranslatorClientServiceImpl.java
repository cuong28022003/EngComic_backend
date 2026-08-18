package mobile.databases.services;

import mobile.apis.translator.dtos.TranslatorResponseDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TranslatorClientServiceImpl implements TranslatorClientService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PYTHON_API_URL = "http://localhost:8000";

    @Override
    public TranslatorResponseDto getIpaAndMeaning(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("text", text), headers);

        try {
            ResponseEntity<TranslatorResponseDto> response = restTemplate.exchange(
                    PYTHON_API_URL + "/api/ipa-meaning",
                    HttpMethod.POST,
                    request,
                    TranslatorResponseDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            return TranslatorResponseDto.builder()
                    .text(text)
                    .meaning("Không thể gọi API Python: " + e.getMessage())
                    .build();
        }
    }
}
