package mobile.domains.gacha;

import java.util.Random;

/**
 * Pure domain rules for Gacha probability and drops.
 */
public class GachaRules {

    private static final Random RANDOM = new Random();

    public static String rollRarity() {
        int roll = RANDOM.nextInt(100);
        if (roll < 2) return "SSR";    // 2%
        if (roll < 10) return "SR";    // 8%
        if (roll < 35) return "R";     // 25%
        return "C";                    // 65%
    }
}
