package mobile.businesses.interactors.comic;

import lombok.RequiredArgsConstructor;
import mobile.apis.comic.dtos.ComicResponseDto;
import mobile.apis.comic.dtos.SavedResponseDto;
import mobile.businesses.boundaries.comic.GetComicDetail;
import mobile.businesses.boundaries.comic.SaveComic;
import mobile.databases.entities.comic.SavedEntity;
import mobile.databases.repositories.comic.SavedRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveComicInteractor implements SaveComic {

    private final SavedRepository savedRepository;
    private final GetComicDetail getComicDetail;

    @Override
    public Response execute(Request request) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getComicId() == null || request.getComicId().isBlank()) {
            throw new IllegalArgumentException("Comic ID is required");
        }

        // Đã lưu trước đó thì trả về bản ghi cũ (idempotent)
        SavedEntity saved = savedRepository
                .findByUserIdAndComicId(request.getUserId(), request.getComicId())
                .orElseGet(() -> savedRepository.save(
                        new SavedEntity(request.getUserId(), request.getComicId())));

        ComicResponseDto comic = null;
        try {
            comic = getComicDetail.execute(GetComicDetail.Request.builder()
                    .id(saved.getComicId())
                    .build()).getComic();
        } catch (Exception ignored) {}

        return Response.builder()
                .saved(SavedResponseDto.builder()
                        .id(saved.getId())
                        .userId(saved.getUserId())
                        .comicId(saved.getComicId())
                        .createdAt(saved.getCreatedAt())
                        .comic(comic)
                        .build())
                .build();
    }
}
