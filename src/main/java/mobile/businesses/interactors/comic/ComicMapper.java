package mobile.businesses.interactors.comic;

import mobile.apis.comic.dtos.ComicResponseDto;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.databases.entities.comic.ComicEntity;
import org.springframework.stereotype.Component;

@Component
public class ComicMapper {

    public ComicResponseDto toDto(ComicEntity entity, UserProfileDto uploader, int totalChapters, double rating, int totalRatings) {
        if (entity == null) return null;

        return ComicResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .url(entity.getUrl())
                .description(entity.getDescription())
                .genre(entity.getGenre())
                .artist(entity.getArtist())
                .imageUrl(entity.getImageUrl())
                .backgroundUrl(entity.getBackgroundUrl())
                .uploader(uploader)
                .views(entity.getViews())
                .rating(rating)
                .totalRatings(totalRatings)
                .totalChapters(totalChapters)
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdateAt())
                .englishLevel(entity.getEnglishLevel())
                .ageRating(entity.getAgeRating())
                .build();
    }
}
