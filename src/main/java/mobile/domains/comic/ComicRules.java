package mobile.domains.comic;

/**
 * Pure domain rules for Comic and Chapter validations.
 */
public class ComicRules {

    public static boolean isValidComicName(String name) {
        return name != null && !name.trim().isBlank() && name.trim().length() <= 200;
    }

    public static boolean isValidChapterNumber(int chapterNumber) {
        return chapterNumber >= 0;
    }

    public static String normalizeSlug(String name) {
        if (name == null) return "";
        return name.trim().toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }
}
