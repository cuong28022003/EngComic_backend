package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.PendingItemResponseDto;
import mobile.businesses.boundaries.vocab.GetPendingItems;
import mobile.databases.entities.vocab.PendingItemEntity;
import mobile.databases.repositories.vocab.PendingItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPendingItemsInteractor implements GetPendingItems {

    private final PendingItemRepository pendingItemRepository;
    private final PendingItemMapper pendingItemMapper;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        String status = request.getStatus();
        Pageable pageable = request.getPageable();

        Page<PendingItemEntity> page;
        if (status != null && !status.trim().isEmpty()) {
            page = pendingItemRepository.findByUserIdAndStatus(userId, status.trim(), pageable);
        } else {
            page = pendingItemRepository.findByUserIdAndStatus(userId, "pending", pageable);
        }

        Page<PendingItemResponseDto> dtoPage = page.map(pendingItemMapper::toResponseDto);
        return Response.builder().items(dtoPage).build();
    }
}

