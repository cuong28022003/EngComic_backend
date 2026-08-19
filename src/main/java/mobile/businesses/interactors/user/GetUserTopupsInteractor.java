package mobile.businesses.interactors.user;

import lombok.RequiredArgsConstructor;
import mobile.apis.user.dtos.TopupResponseDto;
import mobile.businesses.boundaries.user.GetUserTopups;
import mobile.databases.entities.user.TopupEntity;
import mobile.databases.repositories.user.TopupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserTopupsInteractor implements GetUserTopups {

    private final TopupRepository topupRepository;

    @Override
    public Response execute(Request request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        Page<TopupEntity> page = topupRepository.findByUserId(request.getUserId(), request.getPageable());

        List<TopupResponseDto> dtos = page.getContent().stream().map(topup -> TopupResponseDto.builder()
                .id(topup.getId())
                .userId(topup.getUserId())
                .diamond(topup.getDiamond())
                .note(topup.getNote())
                .createdAt(topup.getCreatedAt())
                .processed(topup.isProcessed())
                .canceled(topup.isCanceled())
                .build()).collect(Collectors.toList());

        return Response.builder()
                .topups(new PageImpl<>(dtos, request.getPageable(), page.getTotalElements()))
                .build();
    }
}

