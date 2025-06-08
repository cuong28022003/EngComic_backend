package mobile.Service;

import mobile.model.payload.response.translator.TranslatorResponse;

public interface TranslatorService {
    TranslatorResponse getIpaAndMeaning(String text);
}
