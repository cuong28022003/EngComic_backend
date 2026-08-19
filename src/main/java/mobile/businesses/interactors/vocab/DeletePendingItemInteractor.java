package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.vocab.DeletePendingItem;
import mobile.databases.repositories.vocab.PendingItemRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletePendingItemInteractor implements DeletePendingItem {

    private final PendingItemRepository pendingItemRepository;

    @Override
    public Response execute(Request request) {
        pendingItemRepository.deleteByIdAndUserId(request.getId(), request.getUserId());
        return Response.builder().success(true).build();
    }
}

