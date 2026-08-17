package mobile.businesses.interactors.pendingitem;

import lombok.RequiredArgsConstructor;
import mobile.apis.pendingitem.dtos.PendingItemResponseDto;
import mobile.businesses.boundaries.pendingitem.AddPendingItem;
import mobile.databases.entities.pendingitem.PendingItemEntity;
import mobile.databases.repositories.pendingitem.PendingItemRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddPendingItemInteractor implements AddPendingItem {

    private final PendingItemRepository pendingItemRepository;
    private final PendingItemMapper pendingItemMapper;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        String content = request.getContent();
        String sourceType = request.getSourceType();
        String sourceCardId = request.getSourceCardId();

        if (userId == null || content == null || content.trim().isEmpty()) {
            return Response.builder().item(null).build();
        }

        String cleanContent = content.trim();
        Optional<PendingItemEntity> existing = pendingItemRepository.findByUserIdAndContentIgnoreCase(userId, cleanContent);
        if (existing.isPresent()) {
            return Response.builder().item(pendingItemMapper.toResponseDto(existing.get())).build();
        }

        PendingItemEntity item = new PendingItemEntity();
        item.setUserId(userId);
        item.setContent(cleanContent);
        item.setSourceType(sourceType != null ? sourceType : "manual");
        item.setSourceCardId(sourceCardId);
        item.setStatus("pending");
        item.setCreatedAt(new Date());

        PendingItemEntity saved = pendingItemRepository.save(item);
        PendingItemResponseDto dto = pendingItemMapper.toResponseDto(saved);
        return Response.builder().item(dto).build();
    }
}
