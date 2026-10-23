package mobile.businesses.interactors.comic;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.comic.UnsaveComic;
import mobile.databases.repositories.comic.SavedRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnsaveComicInteractor implements UnsaveComic {

    private final SavedRepository savedRepository;

    @Override
    public Response execute(Request request) {
        if (request.getId() == null || request.getId().isBlank()) {
            throw new IllegalArgumentException("Saved ID is required");
        }
        savedRepository.deleteById(request.getId());
        return Response.builder()
                .success(true)
                .build();
    }
}
