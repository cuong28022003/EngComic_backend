package mobile.databases.services;

import mobile.apis.translator.dtos.TranslatorResponseDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public interface TranslatorClientService {
    TranslatorResponseDto getIpaAndMeaning(String text);
}
