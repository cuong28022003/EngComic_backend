package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.PracticePromptResponseDto;
import mobile.businesses.boundaries.vocab.GeneratePracticePromptBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.entities.vocab.DeckEntity;
import mobile.databases.repositories.vocab.CardRepository;
import mobile.databases.repositories.vocab.DeckRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneratePracticePromptInteractor implements GeneratePracticePromptBoundary {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    @Override
    public Response execute(Request request) {
        String deckId = request.getDeckId();
        String userId = request.getUserId();
        String deckName = "Bộ từ vựng";

        List<CardEntity> cards;
        if (deckId != null && !deckId.isBlank()) {
            cards = cardRepository.findByUserIdAndDeckId(userId, deckId);
            Optional<DeckEntity> deckOpt = deckRepository.findById(deckId);
            if (deckOpt.isPresent()) {
                deckName = deckOpt.get().getName();
            }
        } else {
            cards = cardRepository.findByUserId(userId);
        }

        List<String> wordsList = new ArrayList<>();
        StringBuilder wordSummaryBuilder = new StringBuilder();

        int count = 0;
        for (CardEntity c : cards) {
            // Only extract prompt for pending cards (cards that do NOT have exercisePackage yet)
            if (c.getExercisePackage() != null) {
                continue;
            }

            String word = c.getWord() != null ? c.getWord() : "";
            String meaning = c.getMeaning() != null ? c.getMeaning() : "";
            String pos = c.getPartOfSpeech() != null ? c.getPartOfSpeech() : "";
            String ipa = c.getIpa() != null ? c.getIpa() : "";

            if (!word.isBlank()) {
                count++;
                wordsList.add(word);
                wordSummaryBuilder.append(String.format("%d. %s (%s) [%s]: %s\n",
                        count, word, pos, ipa, meaning));
            }
        }

        String jsonTemplate = """
[
  {
    "word": "từ_tiếng_anh",
    "level1_recognition": {
      "question": "Chọn nghĩa tiếng Việt chính xác nhất của từ:",
      "options": [
        { "text": "Nghĩa đúng của từ", "isCorrect": true },
        { "text": "Đáp án nhiễu 1 (sai nhưng dễ gây nhầm lẫn)", "isCorrect": false },
        { "text": "Đáp án nhiễu 2", "isCorrect": false },
        { "text": "Đáp án nhiễu 3", "isCorrect": false }
      ]
    },
    "level2_context": {
      "question": "Chọn cụm từ (collocation) tự nhiên nhất để điền vào chỗ trống:",
      "sentence": "Câu tiếng Anh có chứa chỗ trống ___ để điền từ.",
      "options": [
        { "text": "từ_đúng", "isCorrect": true },
        { "text": "từ_sai_1", "isCorrect": false },
        { "text": "từ_sai_2", "isCorrect": false },
        { "text": "từ_sai_3", "isCorrect": false }
      ],
      "collocationNote": "Ghi chú cụm từ cố định phổ biến (VD: 'make a decision', 'mitigate risk')"
    },
    "level3_production": {
      "prompt": "Sắp xếp các mảnh từ sau thành câu giao tiếp hoàn chỉnh:",
      "shuffledWords": ["Mảnh1", "Mảnh2", "Mảnh3", "Mảnh4", "Mảnh5"],
      "correctSentence": "Câu hoàn chỉnh đúng ngữ pháp và tự nhiên.",
      "vietnameseMeaning": "Bản dịch nghĩa tiếng Việt của câu."
    },
    "level4_realworld": {
      "situation": "Mô tả tình huống công sở/giao tiếp thực tế đòi hỏi sử dụng từ này.",
      "sampleResponse": "Câu phản hồi hoặc cách diễn đạt mẫu tự nhiên nhất.",
      "keyTakeaways": "Ghi chú sắc thái ngữ nghĩa hoặc mẹo ứng dụng trong thực tế."
    }
  }
]
""";

        String systemPrompt = String.format("""
Bạn là chuyên gia ngôn ngữ học tiếng Anh và thiết kế phương pháp học từ vựng Spaced Repetition (SRS).
Nhiệm vụ của bạn là tạo ngân hàng bài tập thực hành 4 CẤP ĐỘ TINH THÔNG (4-Level Mastery) cho danh sách %d từ vựng sau đây:

=== DANH SÁCH TỪ VỰNG (%s) ===
%s

=== YÊU CẦU THIẾT KẾ CHO TỪNG TỪ VỰNG: ===
1. Level 1 (Nhận biết): Trắc nghiệm chọn nghĩa 4 lựa chọn (1 đúng, 3 nhiễu sắc sảo, hợp lý).
2. Level 2 (Ngữ cảnh & Collocation): Câu ví dụ thực tế có chỗ trống ___ và ghi chú cụm từ đi kèm tự nhiên.
3. Level 3 (Tái hiện chủ động): Tách một câu tiếng Anh tự nhiên thành mảng các mảnh từ (shuffledWords) kèm câu đúng và bản dịch.
4. Level 4 (Tình huống thực tế): 1 tình huống giao tiếp/công sở cụ thể + câu phản hồi mẫu tự nhiên + ghi chú sắc thái.

=== QUY ĐỊNH BẮT BUỘC VỀ OUTPUT: ===
- Chỉ trả về DUY NHẤT một JSON Array hợp lệ (Không bọc giải thích ngoài khối markdown code block ```json ... ```).
- Khớp 100%% với cấu trúc JSON mẫu sau:

%s
""", wordsList.size(), deckName, wordSummaryBuilder.toString(), jsonTemplate);

        PracticePromptResponseDto dto = PracticePromptResponseDto.builder()
                .deckId(deckId)
                .deckName(deckName)
                .wordCount(wordsList.size())
                .words(wordsList)
                .systemPrompt(systemPrompt)
                .jsonTemplate(jsonTemplate)
                .build();

        return Response.builder().data(dto).build();
    }
}
