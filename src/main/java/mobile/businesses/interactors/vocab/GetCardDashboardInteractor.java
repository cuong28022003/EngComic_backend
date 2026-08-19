package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.DashboardResponseDto;
import mobile.businesses.boundaries.vocab.GetCardDashboard;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class GetCardDashboardInteractor implements GetCardDashboard {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        String search = request.getSearch();
        String status = request.getStatus();
        String topic = request.getTopic();
        Pageable pageable = request.getPageable();

        long totalCards = cardRepository.countByUserId(userId);
        long dueToday = cardRepository.countByUserIdAndNextReviewLessThanEqual(userId, new Date());
        long matureCount = cardRepository.countByUserIdAndStatus(userId, "mature");
        long learningCount = cardRepository.countByUserIdAndStatus(userId, "learning");
        long leechCount = cardRepository.countByUserIdAndStatus(userId, "leech");
        long newCount = cardRepository.countByUserIdAndStatus(userId, "new");

        Page<CardEntity> cardPage;
        boolean hasSearch = (search != null && !search.trim().isEmpty());
        boolean hasStatus = (status != null && !status.trim().isEmpty());
        boolean hasTopic = (topic != null && !topic.trim().isEmpty());

        if (hasSearch) {
            cardPage = cardRepository.findByUserIdAndBackContainingIgnoreCaseOrFrontContainingIgnoreCase(userId, search.trim(), search.trim(), pageable);
        } else if (hasStatus) {
            cardPage = cardRepository.findByUserIdAndStatus(userId, status.trim(), pageable);
        } else if (hasTopic) {
            cardPage = cardRepository.findByUserIdAndTopic(userId, topic.trim(), pageable);
        } else {
            cardPage = cardRepository.findByUserId(userId, pageable);
        }

        DashboardResponseDto responseDto = DashboardResponseDto.builder()
                .totalCards(totalCards)
                .dueToday(dueToday)
                .matureCount(matureCount)
                .learningCount(learningCount)
                .leechCount(leechCount)
                .newCount(newCount)
                .cards(cardPage.map(cardMapper::toResponse))
                .build();

        return Response.builder().data(responseDto).build();
    }
}

