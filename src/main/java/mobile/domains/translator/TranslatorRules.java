package mobile.domains.translator;

/**
 * Pure domain rules for Translator.
 */
public class TranslatorRules {

    public static boolean isValidText(String text) {
        return text != null && !text.trim().isBlank() && text.length() <= 5000;
    }
}
