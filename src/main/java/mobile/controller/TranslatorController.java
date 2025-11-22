package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.TranslatorService;
import mobile.model.payload.request.translator.TranslatorRequest;
import mobile.model.payload.response.translator.TranslatorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/translator")
@RequiredArgsConstructor
public class TranslatorController {
    private final TranslatorService translatorService;

    @PostMapping("/ipa-meaning")
    public ResponseEntity<TranslatorResponse> getIpaAndMeaning(@RequestBody TranslatorRequest request) {
        TranslatorResponse response = translatorService.getIpaAndMeaning(request.getText());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ocr")
    public ResponseEntity<TranslatorResponse> extractText(@RequestParam("image")MultipartFile image) {
        TranslatorResponse response = translatorService.extractTextFromImage(image);
        return ResponseEntity.ok(response);
    }
}
