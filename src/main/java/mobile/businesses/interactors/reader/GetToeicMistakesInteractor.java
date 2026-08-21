package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.ToeicMistakeDto;
import mobile.businesses.boundaries.reader.GetToeicMistakesBoundary;
import mobile.databases.entities.reader.ToeicMistakeEntity;
import mobile.databases.repositories.reader.ToeicMistakeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetToeicMistakesInteractor implements GetToeicMistakesBoundary {

    private final ToeicMistakeRepository mistakeRepository;
    private final ToeicReaderMapper mapper;

    @Override
    public Response execute(Request request) {
        Pageable pageable = request.getPageable() != null ? request.getPageable() : Pageable.unpaged();
        Page<ToeicMistakeEntity> page;

        if (request.getStatus() != null && !request.getStatus().trim().isEmpty() && !request.getStatus().equalsIgnoreCase("all")) {
            page = mistakeRepository.findByUserIdAndStatusOrderByQuestionNumberAsc(
                    request.getUserId(), request.getStatus().trim().toLowerCase(), pageable);
        } else {
            page = mistakeRepository.findByUserIdOrderByQuestionNumberAsc(request.getUserId(), pageable);
        }

        return Response.builder()
                .data(page.map(mapper::toMistakeDto))
                .build();
    }
}
