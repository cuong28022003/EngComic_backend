package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.businesses.boundaries.vocab.GetDuePracticeCards;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetDuePracticeCardsInteractor implements GetDuePracticeCards {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        if (userId == null) {
            return Response.builder().cards(Collections.emptyList()).build();
        }

        List<CardEntity> dueList = cardRepository.findDueCardsByUserId(userId, new Date());

        // Sort priority: leech first -> overdue -> new
        dueList.sort((c1, c2) -> {
            boolean l1 = "leech".equalsIgnoreCase(c1.getStatus());
            boolean l2 = "leech".equalsIgnoreCase(c2.getStatus());
            if (l1 && !l2) return -1;
            if (!l1 && l2) return 1;

            if (c1.getNextReview() != null && c2.getNextReview() != null) {
                return c1.getNextReview().compareTo(c2.getNextReview());
            }
            return 0;
        });

        int limit = request.getLimit() > 0 ? request.getLimit() : 15;
        int max = Math.min(limit, dueList.size());
        List<CardResponseDto> responses = dueList.subList(0, max).stream()
                .map(cardMapper::toResponse)
                .collect(Collectors.toList());

        return Response.builder().cards(responses).build();
    }
}

