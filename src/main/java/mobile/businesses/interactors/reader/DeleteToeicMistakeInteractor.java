package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.reader.DeleteToeicMistakeBoundary;
import mobile.databases.entities.reader.ToeicMistakeEntity;
import mobile.databases.repositories.reader.ToeicMistakeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DeleteToeicMistakeInteractor implements DeleteToeicMistakeBoundary {

    private final ToeicMistakeRepository mistakeRepository;

    @Override
    @Transactional
    public Response execute(Request request) {
        ToeicMistakeEntity mistake = mistakeRepository.findByIdAndUserId(request.getMistakeId(), request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lỗi trong hàng đợi"));

        mistakeRepository.delete(mistake);
        return Response.builder().success(true).build();
    }
}
