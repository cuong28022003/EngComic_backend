package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.ToeicTestDetailDto;
import mobile.businesses.boundaries.reader.GetToeicTestDetailBoundary;
import mobile.databases.entities.reader.ToeicTestEntity;
import mobile.databases.repositories.reader.ToeicTestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GetToeicTestDetailInteractor implements GetToeicTestDetailBoundary {

    private final ToeicTestRepository testRepository;
    private final ToeicReaderMapper mapper;

    @Override
    public Response execute(Request request) {
        ToeicTestEntity test = testRepository.findByIdAndUserId(request.getTestId(), request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài thi"));

        return Response.builder()
                .data(mapper.toDetailDto(test))
                .build();
    }
}
