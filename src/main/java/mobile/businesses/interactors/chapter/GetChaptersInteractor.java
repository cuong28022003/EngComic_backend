package mobile.businesses.interactors.chapter;

import lombok.RequiredArgsConstructor;
import mobile.apis.chapter.dtos.ChapterResponseDto;
import mobile.businesses.boundaries.chapter.GetChapterDetail;
import mobile.businesses.boundaries.chapter.GetChapters;
import mobile.databases.entities.chapter.ChapterEntity;
import mobile.databases.repositories.chapter.ChapterRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetChaptersInteractor implements GetChapters, GetChapterDetail {

    private final ChapterRepository chapterRepository;
    private final ChapterMapper chapterMapper;

    @Override
    public GetChapters.Response execute(GetChapters.Request request) {
        if (request.getComicId() == null) {
            throw new IllegalArgumentException("Comic ID is required");
        }

        Page<ChapterEntity> page = chapterRepository.findByComicId(request.getComicId(), request.getPageable());
        Page<ChapterResponseDto> dtoPage = page.map(chapterMapper::toDto);

        return GetChapters.Response.builder()
                .chapters(dtoPage)
                .build();
    }

    @Override
    public GetChapterDetail.Response execute(GetChapterDetail.Request request) {
        if (request.getChapterId() == null) {
            throw new IllegalArgumentException("Chapter ID is required");
        }

        ChapterEntity chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found: " + request.getChapterId()));

        return GetChapterDetail.Response.builder()
                .chapter(chapterMapper.toDto(chapter))
                .build();
    }
}
