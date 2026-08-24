package mobile.businesses.interactors.vocab;

import lombok.RequiredArgsConstructor;
import mobile.apis.vocab.dtos.CardResponseDto;
import mobile.businesses.boundaries.vocab.ClearLeechStatusBoundary;
import mobile.databases.entities.vocab.CardEntity;
import mobile.databases.repositories.vocab.CardRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClearLeechStatusInteractor implements ClearLeechStatusBoundary {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        String userId = request.getUserId();
        String cardId = request.getCardId();
        String memoryTip = request.getMemoryTip();

        Optional<CardEntity> cardOpt = cardRepository.findByIdAndUserId(cardId, userId);
        if (cardOpt.isEmpty()) {
            return Response.builder()
                    .message("Không tìm thấy thẻ từ vựng")
                    .build();
        }

        CardEntity card = cardOpt.get();
        if (memoryTip != null && !memoryTip.isBlank()) {
            card.setMemoryTip(memoryTip.trim());
        }

        // Reset Leech status to learning and restart at Level 1
        card.setStatus("learning");
        card.setMasteryLevel(1);
        card.setWrongCount(0);
        card.setInterval(1);

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        card.setNextReview(cal.getTime());
        card.setUpdateAt(now);

        cardRepository.save(card);

        CardResponseDto dto = cardMapper.toResponse(card);

        return Response.builder()
                .card(dto)
                .message("Đã gỡ trạng thái Leech thành công! Từ vựng đã sẵn sàng ôn tập từ Level 1.")
                .build();
    }
}
