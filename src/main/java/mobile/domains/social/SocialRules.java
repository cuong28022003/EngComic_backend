package mobile.domains.social;

/**
 * Pure domain rules for Social operations (Rating, Comment, Report).
 */
public class SocialRules {

    public static boolean isValidRatingScore(int rating) {
        return rating >= 1 && rating <= 5;
    }

    public static boolean isValidComment(String content) {
        return content != null && !content.trim().isBlank() && content.length() <= 1000;
    }

    public static boolean isValidReportReason(String reason) {
        return reason != null && !reason.trim().isBlank();
    }
}
