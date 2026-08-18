package mobile.domains.economy;

/**
 * Pure domain rules for Economy (XP, Streak, Topup).
 */
public class EconomyRules {

    public static int calculateStreak(int currentStreak, boolean isContinuous) {
        return isContinuous ? currentStreak + 1 : 1;
    }

    public static boolean isValidTopupAmount(int diamond) {
        return diamond > 0;
    }
}
