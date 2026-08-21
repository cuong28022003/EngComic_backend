package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.ToeicMistakeDto;
import mobile.apis.reader.dtos.UpdateMistakeRequest;
import mobile.businesses.boundaries.reader.UpdateToeicMistakeBoundary;
import mobile.databases.entities.reader.ToeicMistakeEntity;
import mobile.databases.repositories.reader.ToeicMistakeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UpdateToeicMistakeInteractor implements UpdateToeicMistakeBoundary {

    private final ToeicMistakeRepository mistakeRepository;
    private final ToeicReaderMapper mapper;

    @Override
    @Transactional
    public Response execute(Request request) {
        ToeicMistakeEntity mistake = mistakeRepository.findByIdAndUserId(request.getMistakeId(), request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lỗi trong hàng đợi"));

        UpdateMistakeRequest update = request.getUpdateData();
        if (update != null) {
            if (update.getExplanation() != null) {
                mistake.setExplanation(update.getExplanation());
                if ("pending".equalsIgnoreCase(mistake.getStatus())) {
                    mistake.setStatus("explained");
                }
            }
            if (update.getStatus() != null && !update.getStatus().trim().isEmpty()) {
                mistake.setStatus(update.getStatus().trim().toLowerCase());
            }
            mistake.setUpdatedAt(new Date());
        }

        ToeicMistakeEntity saved = mistakeRepository.save(mistake);
        return Response.builder()
                .data(mapper.toMistakeDto(saved))
                .build();
    }
}
