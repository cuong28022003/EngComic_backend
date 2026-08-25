package mobile.databases.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.apis.translator.dtos.TranslatorResponseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    private static final String GOOGLE_TRANSLATE_URL = "https://translate.google.com/translate_a/single";
    private static final String MYMEMORY_TRANSLATE_URL = "https://api.mymemory.translated.net/get";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    @Override
    public TranslatorResponseDto getIpaAndMeaning(String text) {
        if (text == null || text.trim().isEmpty()) {
            return TranslatorResponseDto.builder()
                    .text("")
                    .meaning("")
                    .build();
        }

        String cleanText = text.trim();

        // 1. Try Google Translate with Chrome extension client ID and User-Agent
        String googleMeaning = translateWithGoogle(cleanText);
        if (googleMeaning != null && !googleMeaning.isEmpty() && !googleMeaning.equalsIgnoreCase(cleanText)) {
            return TranslatorResponseDto.builder()
                    .text(cleanText)
                    .meaning(googleMeaning)
                    .build();
        }

        // 2. Fallback to MyMemory translation API
        String myMemoryMeaning = translateWithMyMemory(cleanText);
        if (myMemoryMeaning != null && !myMemoryMeaning.isEmpty() && !myMemoryMeaning.equalsIgnoreCase(cleanText)) {
            return TranslatorResponseDto.builder()
                    .text(cleanText)
                    .meaning(myMemoryMeaning)
                    .build();
        }

        return TranslatorResponseDto.builder()
                .text(cleanText)
                .meaning(googleMeaning != null ? googleMeaning : cleanText)
                .build();
    }

    private String translateWithGoogle(String text) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(GOOGLE_TRANSLATE_URL)
                    .queryParam("client", "dict-chrome-ex")
                    .queryParam("sl", "en")
                    .queryParam("tl", "vi")
                    .queryParam("dt", "t")
                    .queryParam("q", text)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", USER_AGENT);
            headers.set("Accept", "*/*");
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);

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
                    if (!translated.isEmpty()) {
                        return translated;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Google Translate error for '{}': {}", text, e.getMessage());
        }
        return null;
    }

    private String translateWithMyMemory(String text) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(MYMEMORY_TRANSLATE_URL)
                    .queryParam("q", text)
                    .queryParam("langpair", "en|vi")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", USER_AGENT);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode resData = root.path("responseData");
                if (!resData.isMissingNode()) {
                    String translated = resData.path("translatedText").asText("").trim();
                    if (!translated.isEmpty()) {
                        return translated;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("MyMemory translate error for '{}': {}", text, e.getMessage());
        }
        return null;
    }
}
