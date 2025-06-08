package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.TranslatorService;
import mobile.model.payload.request.translator.TranslatorRequest;
import mobile.model.payload.response.translator.TranslatorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
