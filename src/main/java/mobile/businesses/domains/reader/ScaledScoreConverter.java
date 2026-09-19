package mobile.businesses.domains.reader;

/**
 * Chuyển đổi raw score (0-100) sang scaled score (5-495).
 * Bảng chuẩn công khai từ các nhà cung cấp luyện thi TOEIC.
 */
public final class ScaledScoreConverter {

    private static final int[] LISTENING_SCALE = {
            5, 5, 5, 10, 10, 15, 20, 25, 30, 35,
            40, 45, 50, 55, 60, 65, 70, 75, 80, 85,
            90, 95, 100, 105, 110, 115, 120, 125, 130, 135,
            140, 145, 150, 155, 160, 165, 170, 175, 180, 185,
            190, 195, 200, 205, 210, 215, 220, 225, 230, 235,
            240, 245, 250, 255, 260, 265, 270, 275, 280, 285,
            290, 295, 300, 310, 315, 320, 325, 330, 335, 340,
            345, 350, 355, 360, 365, 370, 375, 380, 385, 390,
            395, 400, 405, 410, 415, 420, 425, 430, 435, 440,
            445, 450, 455, 460, 465, 470, 475, 480, 485, 490,
            495
    };

    private static final int[] READING_SCALE = {
            5, 5, 5, 10, 15, 20, 30, 35, 40, 45,
            50, 55, 60, 65, 70, 75, 80, 85, 90, 95,
            100, 105, 110, 115, 120, 125, 130, 140, 150, 155,
            165, 170, 180, 190, 200, 205, 215, 225, 235, 245,
            250, 260, 270, 280, 290, 300, 310, 315, 325, 335,
            345, 355, 365, 375, 380, 390, 395, 400, 410, 420,
            425, 435, 440, 450, 455, 465, 470, 475, 485, 490,
            495, 495, 495, 495, 495, 495, 495, 495, 495, 495,
            495, 495, 495, 495, 495, 495, 495, 495, 495, 495,
            495, 495, 495, 495, 495, 495, 495, 495, 495, 495,
            495
    };

    private ScaledScoreConverter() {
    }

    /**
     * Quy đổi raw score sang scaled score theo section.
     *
     * @param rawScore  số câu đúng (theo tổng câu đã làm)
     * @param totalQuestions tổng câu đã làm (dùng để quy về thang 100 nếu chọn part riêng)
     * @param section   "reading" | "listening"
     * @return scaled score 5-495, hoặc null nếu đầu vào không hợp lệ
     */
    public static Integer convert(int rawScore, int totalQuestions, String section) {
        if (rawScore < 0 || totalQuestions <= 0) {
            return null;
        }
        double ratio = (double) rawScore / totalQuestions;
        int adjusted = (int) Math.round(ratio * 100);
        if (adjusted < 0) adjusted = 0;
        if (adjusted > 100) adjusted = 100;

        int[] table = "listening".equalsIgnoreCase(section) ? LISTENING_SCALE : READING_SCALE;
        int value = table[adjusted];
        return Math.max(5, Math.min(495, value));
    }
}