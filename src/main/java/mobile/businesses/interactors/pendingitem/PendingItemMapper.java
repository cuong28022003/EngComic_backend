package mobile.businesses.interactors.pendingitem;

import mobile.apis.pendingitem.dtos.PendingItemResponseDto;
import mobile.databases.entities.pendingitem.PendingItemEntity;
import org.springframework.stereotype.Component;

@Component
public class PendingItemMapper {

    public PendingItemResponseDto toResponseDto(PendingItemEntity entity) {
        if (entity == null) return null;
        PendingItemResponseDto dto = new PendingItemResponseDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setContent(entity.getContent());
        dto.setSourceType(entity.getSourceType());
        dto.setSourceCardId(entity.getSourceCardId());
        dto.setStatus(entity.getStatus());
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().toString());
        }
        return dto;
    }
}
