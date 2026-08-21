package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.CreateMistakeBatchRequest;
import mobile.apis.reader.dtos.ToeicMistakeDto;
import mobile.businesses.boundaries.reader.CreateMistakeBatchBoundary;
import mobile.databases.entities.reader.ToeicMistakeEntity;
import mobile.databases.repositories.reader.ToeicMistakeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateMistakeBatchInteractor implements CreateMistakeBatchBoundary {

    private final ToeicMistakeRepository mistakeRepository;
    private final ToeicReaderMapper mapper;

    @Override
    @Transactional
    public Response execute(Request request) {
        CreateMistakeBatchRequest data = request.getBatchData();
        if (data == null || data.getMistakes() == null || data.getMistakes().isEmpty()) {
            return Response.builder().data(Collections.emptyList()).build();
        }

        List<ToeicMistakeEntity> entities = data.getMistakes().stream()
                .map(item -> ToeicMistakeEntity.builder()
                        .userId(request.getUserId())
                        .testId(item.getTestId())
                        .testName(item.getTestName())
                        .questionNumber(item.getQuestionNumber())
                        .part(item.getPart())
                        .userAnswer(item.getUserAnswer())
                        .correctAnswer(item.getCorrectAnswer())
                        .status("pending")
                        .createdAt(new Date())
                        .updatedAt(new Date())
                        .build())
                .collect(Collectors.toList());

        List<ToeicMistakeEntity> saved = mistakeRepository.saveAll(entities);
        return Response.builder()
                .data(saved.stream().map(mapper::toMistakeDto).collect(Collectors.toList()))
                .build();
    }
}
