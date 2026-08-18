package mobile.businesses.interactors.reading;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;
import mobile.apis.reading.dtos.ReadingResponseDto;
import mobile.businesses.boundaries.comic.GetComicDetail;
import mobile.businesses.boundaries.reading.GetUserReadings;
import mobile.databases.entities.reading.ReadingEntity;
import mobile.databases.repositories.reading.ReadingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserReadingsInteractor implements GetUserReadings {

    private final ReadingRepository readingRepository;
    private final GetComicDetail getComicDetail;

    @Override
    public Response execute(Request request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        Page<ReadingEntity> page = readingRepository.findByUserId(request.getUserId(), request.getPageable());

        List<ReadingResponseDto> dtos = page.getContent().stream().map(reading -> {
            ComicResponseDto comic = null;
            if (reading.getComicId() != null) {
                try {
                    comic = getComicDetail.execute(GetComicDetail.Request.builder()
                            .id(reading.getComicId())
                            .build()).getComic();
                } catch (Exception ignored) {}
            }
            return ReadingResponseDto.builder()
                    .id(reading.getId())
                    .userId(reading.getUserId())
                    .comicId(reading.getComicId())
                    .chapterNumber(reading.getChapterNumber())
                    .comic(comic)
                    .build();
        }).collect(Collectors.toList());

        return Response.builder()
                .readings(new PageImpl<>(dtos, request.getPageable(), page.getTotalElements()))
                .build();
    }
}
