package mobile.businesses.interactors.reader;

import lombok.RequiredArgsConstructor;
import mobile.apis.reader.dtos.ToeicTestSummaryDto;
import mobile.businesses.boundaries.reader.GetToeicTestListBoundary;
import mobile.databases.entities.reader.ToeicTestEntity;
import mobile.databases.repositories.reader.ToeicTestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetToeicTestListInteractor implements GetToeicTestListBoundary {

    private final ToeicTestRepository testRepository;
    private final ToeicReaderMapper mapper;

    @Override
    public Response execute(Request request) {
        Pageable pageable = request.getPageable() != null ? request.getPageable() : Pageable.unpaged();
        Page<ToeicTestEntity> entityPage = testRepository.findByUserIdOrderByCreatedAtDesc(request.getUserId(), pageable);
        Page<ToeicTestSummaryDto> dtoPage = entityPage.map(mapper::toSummaryDto);

        return Response.builder()
                .data(dtoPage)
                .build();
    }
}
