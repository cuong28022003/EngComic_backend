package mobile.businesses.interactors.grammar;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.grammar.DeleteGrammarPointBoundary;
import mobile.databases.repositories.grammar.GrammarPointRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteGrammarPointInteractor implements DeleteGrammarPointBoundary {

    private final GrammarPointRepository grammarPointRepository;

    @Override
    public Response execute(Request request) {
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            return Response.builder()
                    .success(false)
                    .message("ID điểm ngữ pháp không hợp lệ")
                    .build();
        }

        grammarPointRepository.deleteById(request.getId().trim());

        return Response.builder()
                .success(true)
                .message("Đã xóa điểm ngữ pháp thành công")
                .build();
    }
}
