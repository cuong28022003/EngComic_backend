package mobile.domains.comic;

/**
 * Pure domain rules for Comic, Chapter and Social interactions (ratings, comments, reports).
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

    public static boolean isValidRating(int score) {
        return score >= 1 && score <= 5;
    }

    public static boolean isValidCommentContent(String content) {
        return content != null && !content.trim().isBlank() && content.length() <= 1000;
    }

    public static boolean isValidReportReason(String reason) {
        return reason != null && !reason.trim().isBlank() && reason.length() <= 500;
    }
}
