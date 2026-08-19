package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.vocab.GeneratePrompt;
import mobile.databases.entities.vocab.PendingItemEntity;
import mobile.databases.repositories.vocab.PendingItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneratePromptInteractor implements GeneratePrompt {

    private final PendingItemRepository pendingItemRepository;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        List<PendingItemEntity> pendingList = pendingItemRepository.findByUserIdAndStatus(userId, "pending");
        if (pendingList.isEmpty()) {
            return Response.builder().prompt("Không có từ nào trong danh sách chờ.").build();
        }

        String wordsList = pendingList.stream()
                .map(PendingItemEntity::getContent)
                .filter(c -> c != null && !c.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.joining(", "));

        String prompt = "Với mỗi mục trong danh sách sau: [" + wordsList + "]\n" +
                "Trả về JSON array, mỗi phần tử đúng theo schema:\n" +
                "{\n" +
                "  \"word\": \"...\",\n" +
                "  \"ipa\": \"/.../\",\n" +
                "  \"part_of_speech\": \"...\",\n" +
                "  \"meaning_vi\": \"...\",\n" +
                "  \"definition_en\": \"...\",\n" +
                "  \"usage_note\": null,\n" +
                "  \"topic\": \"general\",\n" +
                "  \"examples\": [\n" +
                "    { \"text\": \"...\", \"formality\": \"formal\" },\n" +
                "    { \"text\": \"...\", \"formality\": \"informal\" },\n" +
                "    { \"text\": \"...\", \"formality\": \"written\" }\n" +
                "  ],\n" +
                "  \"relations\": [\n" +
                "    { \"text\": \"...\", \"type\": \"family\", \"pos\": \"verb\" },\n" +
                "    { \"text\": \"...\", \"type\": \"collocation\" },\n" +
                "    { \"text\": \"...\", \"type\": \"synonym\" }\n" +
                "  ]\n" +
                "}\n" +
                "Nếu mục là cụm từ (collocation), ipa và part_of_speech có thể null.\n" +
                "Chỉ trả JSON thuần, không markdown, không giải thích thêm.";

        return Response.builder().prompt(prompt).build();
    }
}

