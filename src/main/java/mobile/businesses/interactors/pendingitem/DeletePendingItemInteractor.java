package mobile.businesses.interactors.pendingitem;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.pendingitem.DeletePendingItem;
import mobile.databases.repositories.pendingitem.PendingItemRepository;
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
