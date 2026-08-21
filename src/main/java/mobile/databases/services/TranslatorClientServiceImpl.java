package mobile.databases.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.translator.dtos.TranslatorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslatorClientServiceImpl implements TranslatorClientService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private static final String GOOGLE_TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single";

    @Override
    public TranslatorResponseDto getIpaAndMeaning(String text) {
        if (text == null || text.trim().isEmpty()) {
            return TranslatorResponseDto.builder()
                    .text("")
                    .meaning("")
                    .build();
        }

        String cleanText = text.trim();

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(GOOGLE_TRANSLATE_URL)
                    .queryParam("client", "gtx")
                    .queryParam("sl", "en")
                    .queryParam("tl", "vi")
                    .queryParam("dt", "t")
                    .queryParam("q", cleanText)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.isArray() && root.size() > 0) {
                    JsonNode sentences = root.get(0);
                    StringBuilder translatedSb = new StringBuilder();

                    if (sentences.isArray()) {
                        for (JsonNode sentence : sentences) {
                            if (sentence.isArray() && sentence.size() > 0) {
                                translatedSb.append(sentence.get(0).asText());
                            }
                        }
                    }

                    String translated = translatedSb.toString().trim();

                    return TranslatorResponseDto.builder()
                            .text(cleanText)
                            .meaning(translated.isEmpty() ? cleanText : translated)
                            .build();
                }
            }
        } catch (Exception e) {
            log.error("Google Translate API error for text '{}': {}", cleanText, e.getMessage());
        }

        return TranslatorResponseDto.builder()
                .text(cleanText)
                .meaning(cleanText)
                .build();
    }
}
