package mobile.businesses.interactors.saved;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;
import mobile.apis.saved.dtos.SavedResponseDto;
import mobile.businesses.boundaries.comic.GetComicDetail;
import mobile.businesses.boundaries.saved.GetUserSavedComics;
import mobile.databases.entities.saved.SavedEntity;
import mobile.databases.repositories.saved.SavedRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserSavedComicsInteractor implements GetUserSavedComics {

    private final SavedRepository savedRepository;
    private final GetComicDetail getComicDetail;

    @Override
    public Response execute(Request request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        Page<SavedEntity> page = savedRepository.findByUserId(request.getUserId(), request.getPageable());

        List<SavedResponseDto> dtos = page.getContent().stream().map(saved -> {
            ComicResponseDto comic = null;
            if (saved.getComicId() != null) {
                try {
                    comic = getComicDetail.execute(GetComicDetail.Request.builder()
                            .id(saved.getComicId())
                            .build()).getComic();
                } catch (Exception ignored) {}
            }
            return SavedResponseDto.builder()
                    .id(saved.getId())
                    .userId(saved.getUserId())
                    .comicId(saved.getComicId())
                    .createdAt(saved.getCreatedAt())
                    .comic(comic)
                    .build();
        }).collect(Collectors.toList());

        return Response.builder()
                .savedComics(new PageImpl<>(dtos, request.getPageable(), page.getTotalElements()))
                .build();
    }
}
