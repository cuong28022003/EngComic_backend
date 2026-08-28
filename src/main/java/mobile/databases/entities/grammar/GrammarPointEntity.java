package mobile.databases.entities.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "grammar_points")
public class GrammarPointEntity {

    @Id
    private String id;

    /** Tên chủ điểm ngữ pháp (VD: "Hiện tại hoàn thành (Present Perfect)") */
    @Indexed(unique = true)
    private String topic;

    /** Nhóm danh mục (VD: "tenses", "conditionals", "passive_voice"...) */
    @Indexed
    private String category;

    /** Cấp độ: "basic" | "intermediate" | "advanced" | "450" | "650" | "800+" */
    private String level;

    /** Bản chất cốt lõi tóm tắt trong 1-2 câu ngắn gọn */
    private String summary;

    /** Legacy fallback field: Cấu trúc chung */
    private String structure;

    /** Legacy fallback field: Dấu hiệu nhận biết chung */
    private List<String> signalWords;

    /** Legacy fallback field: Lỗi sai chung */
    private String commonMistake;

    /** 
     * Danh sách CÁC CÁCH DÙNG CHI TIẾT (Multi-usages)
     * Mỗi cách dùng có công thức, dấu hiệu và ví dụ minh họa riêng biệt
     */
    private List<GrammarUsage> usages;

    /** Bẫy đề thi & Lỗi sai kinh điển (Multiple common mistakes) */
    private List<String> commonMistakes;

    /** Bảng so sánh phân biệt với các điểm ngữ pháp dễ gây nhầm lẫn */
    private List<GrammarComparison> comparisons;

    /** Từ khóa phục vụ tìm kiếm nhanh (Search Keywords / Tags) */
    private List<String> searchKeywords;

    private Date createdAt;

    private Date updatedAt;

    // ─────────────────────────────────────────────────────────────────
    // Sub-document: Chi tiết 1 Cách Dùng (Grammar Usage)
    // ─────────────────────────────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarUsage {
        /** Tên cách dùng (VD: "Hành động bắt đầu trong quá khứ kéo dài đến hiện tại") */
        private String title;

        /** Công thức / Cấu trúc của cách dùng này (VD: "S + have/has + V3/ed + since/for...") */
        private String structure;

        /** Giải thích chi tiết ngữ cảnh sử dụng */
        private String explanation;

        /** Dấu hiệu nhận biết riêng (Signal words: since, for, so far, up to now...) */
        private List<String> signalWords;

        /** Danh sách câu ví dụ cho cách dùng này */
        private List<GrammarExample> examples;

        /** Lưu ý riêng cho cách dùng này (nếu có) */
        private String note;
    }

    // ─────────────────────────────────────────────────────────────────
    // Sub-document: Câu Ví Dụ Ngữ Cảnh (Grammar Example)
    // ─────────────────────────────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarExample {
        /** Câu tiếng Anh (VD: "We have lived in Hanoi since 2018.") */
        private String text;

        /** Bản dịch tiếng Việt (VD: "Chúng tôi đã sống ở Hà Nội từ năm 2018.") */
        private String translation;

        /** Cụm từ cần highlight trong câu (VD: "have lived in Hanoi since") */
        private String highlight;

        /** Phân tích chi tiết ngữ cảnh câu */
        private String note;
    }

    // ─────────────────────────────────────────────────────────────────
    // Sub-document: So Sánh Phân Biệt (Grammar Comparison)
    // ─────────────────────────────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrammarComparison {
        /** Đối tượng so sánh (VD: "Quá khứ đơn (Past Simple)") */
        private String compareWith;

        /** Điểm khác biệt cốt lõi (VD: "Quá khứ đơn đã chấm dứt hoàn toàn, HTHT còn liên quan đến hiện tại") */
        private String coreDifference;

        /** Câu ví dụ của cấu trúc hiện tại (VD: "I have lost my key. (Hiện tại vẫn chưa tìm thấy)") */
        private String currentExample;

        /** Câu ví dụ đối chiếu (VD: "I lost my key yesterday. (Chỉ kể sự việc hôm qua)") */
        private String targetExample;
    }
}
