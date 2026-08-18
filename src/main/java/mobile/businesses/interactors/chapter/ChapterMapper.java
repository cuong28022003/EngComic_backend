package mobile.businesses.interactors.chapter;

import mobile.apis.chapter.dtos.ChapterResponseDto;
import mobile.databases.entities.chapter.ChapterEntity;
import org.springframework.stereotype.Component;

@Component
public class ChapterMapper {

    public ChapterResponseDto toDto(ChapterEntity entity) {
        if (entity == null) return null;

        return ChapterResponseDto.builder()
                .id(entity.getId())
                .chapterNumber(entity.getChapterNumber())
                .name(entity.getName())
                .imageUrl(entity.getImageUrl())
                .comicId(entity.getComicId())
                .pageUrls(entity.getPageUrls())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
