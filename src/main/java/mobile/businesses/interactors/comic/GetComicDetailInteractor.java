package mobile.businesses.interactors.comic;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.businesses.boundaries.comic.GetComicDetail;
import mobile.businesses.boundaries.user.GetUserProfile;
import mobile.databases.entities.comic.ComicEntity;
import mobile.databases.repositories.comic.ChapterRepository;
import mobile.databases.repositories.comic.ComicRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetComicDetailInteractor implements GetComicDetail {

    private final ComicRepository comicRepository;
    private final ChapterRepository chapterRepository;
    private final GetUserProfile getUserProfile;
    private final ComicMapper comicMapper;

    @Override
    public Response execute(Request request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Comic ID is required");
        }

        ComicEntity comic = comicRepository.findById(request.getId())
                .or(() -> comicRepository.findByUrl(request.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Comic not found: " + request.getId()));

        UserProfileDto uploader = null;
        if (comic.getUploaderId() != null) {
            try {
                uploader = getUserProfile.execute(GetUserProfile.Request.builder()
                        .userId(comic.getUploaderId())
                        .build()).getProfile();
            } catch (Exception ignored) {}
        }

        int totalChapters = (int) chapterRepository.countByComicId(comic.getId());
        ComicResponseDto dto = comicMapper.toDto(comic, uploader, totalChapters, 5.0, 0);

        return Response.builder()
                .comic(dto)
                .build();
    }
}
