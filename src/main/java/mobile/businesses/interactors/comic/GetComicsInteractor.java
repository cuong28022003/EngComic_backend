package mobile.businesses.interactors.comic;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.businesses.boundaries.comic.GetComics;
import mobile.businesses.boundaries.user.GetUserProfile;
import mobile.databases.entities.comic.ComicEntity;
import mobile.databases.repositories.chapter.ChapterRepository;
import mobile.databases.repositories.comic.ComicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetComicsInteractor implements GetComics {

    private final ComicRepository comicRepository;
    private final ChapterRepository chapterRepository;
    private final GetUserProfile getUserProfile;
    private final ComicMapper comicMapper;

    @Override
    public Response execute(Request request) {
        Page<ComicEntity> entityPage;
        if (request.getStatus() != null && !request.getStatus().equalsIgnoreCase("None")) {
            entityPage = comicRepository.findByStatus(request.getStatus(), request.getPageable());
        } else {
            entityPage = comicRepository.findAll(request.getPageable());
        }

        List<ComicResponseDto> dtos = entityPage.getContent().stream().map(comic -> {
            UserProfileDto uploader = null;
            if (comic.getUploaderId() != null) {
                try {
                    uploader = getUserProfile.execute(GetUserProfile.Request.builder()
                            .userId(comic.getUploaderId())
                            .build()).getProfile();
                } catch (Exception ignored) {}
            }
            int totalChapters = (int) chapterRepository.countByComicId(comic.getId());
            return comicMapper.toDto(comic, uploader, totalChapters, 5.0, 0);
        }).collect(Collectors.toList());

        return Response.builder()
                .comics(new PageImpl<>(dtos, request.getPageable(), entityPage.getTotalElements()))
                .build();
    }
}
