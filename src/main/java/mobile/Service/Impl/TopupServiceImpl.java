package mobile.Service.Impl;

import lombok.RequiredArgsConstructor;
import mobile.Service.TopupService;
import mobile.Service.UserStatsService;
import mobile.model.Entity.Topup;
import mobile.model.payload.request.topup.TopupRequest;
import mobile.repository.TopupRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopupServiceImpl implements TopupService {

    private final TopupRepository topupRepository;

    private final UserStatsService userStatsService;

    @Override
    public Topup save(TopupRequest topupRequest) {
        Topup topup = new Topup();
        topup.setUserId(new ObjectId(topupRequest.getUserId()));
        topup.setDiamond(topupRequest.getDiamond());
        topup.setNote(topupRequest.getNote());
        topup.setCreatedAt(LocalDateTime.now()); // Set the current time as createdAt
        topup.setProcessed(false); // Default to false, indicating the request is not yet processed
        return topupRepository.save(topup);
    }

    @Override
    public Topup confirmTopup(ObjectId topupId) {
        Topup topup = topupRepository.findById(topupId)
                .orElseThrow(() -> new RuntimeException("Topup request not found"));
        if (topup.isProcessed()) {
            throw new RuntimeException("Request already processed");
        }

        userStatsService.addDiamond(topup.getUserId(), topup.getDiamond());

        topup.setProcessed(true); // Mark the topup as processed
        return topupRepository.save(topup);
    }

    public List<Topup> findByUserId(ObjectId userId) {
        return topupRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void cancelTopup(ObjectId topupId) {
        Topup topup = topupRepository.findById(topupId)
                .orElseThrow(() -> new RuntimeException("Topup request not found"));

        if (topup.isProcessed()) {
            throw new RuntimeException("Cannot cancel a processed topup request");
        }

        topup.setCanceled(true); // Set the canceled flag to true
        topupRepository.save(topup); // Save the updated topup
    }
}
