package mobile.businesses.interactors.rating;

import lombok.RequiredArgsConstructor;
import mobile.apis.rating.dtos.RatingResponseDto;
import mobile.apis.user.dtos.UserProfileDto;
import mobile.businesses.boundaries.rating.GetComicRatings;
import mobile.businesses.boundaries.user.GetUserProfile;
import mobile.databases.entities.rating.RatingEntity;
import mobile.databases.repositories.rating.RatingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetComicRatingsInteractor implements GetComicRatings {

    private final RatingRepository ratingRepository;
    private final GetUserProfile getUserProfile;

    @Override
    public Response execute(Request request) {
        if (request.getComicId() == null) {
            throw new IllegalArgumentException("Comic ID is required");
        }

        Page<RatingEntity> page = ratingRepository.findByComicId(request.getComicId(), request.getPageable());

        List<RatingResponseDto> dtos = page.getContent().stream().map(rating -> {
            UserProfileDto user = null;
            if (rating.getUserId() != null) {
                try {
                    user = getUserProfile.execute(GetUserProfile.Request.builder()
                            .userId(rating.getUserId())
                            .build()).getProfile();
                } catch (Exception ignored) {}
            }
            return RatingResponseDto.builder()
                    .id(rating.getId())
                    .userId(rating.getUserId())
                    .comicId(rating.getComicId())
                    .rating(rating.getRating())
                    .comment(rating.getComment())
                    .createdAt(rating.getCreatedAt())
                    .updatedAt(rating.getUpdatedAt())
                    .user(user)
                    .build();
        }).collect(Collectors.toList());

        return Response.builder()
                .ratings(new PageImpl<>(dtos, request.getPageable(), page.getTotalElements()))
                .build();
    }
}
