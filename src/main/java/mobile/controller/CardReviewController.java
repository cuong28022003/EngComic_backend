package mobile.controller;

import lombok.RequiredArgsConstructor;
import mobile.Service.CardReviewService;
import mobile.Service.UserService;
import mobile.mapping.CardReviewMapping;
import mobile.model.Entity.CardReview;
import mobile.model.Entity.User;
import mobile.model.payload.response.CardReviewResponse;
import mobile.security.JWT.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/card-review")
@RequiredArgsConstructor
public class CardReviewController {

    private final CardReviewService cardReviewService;
    private final UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/due/{userId}")
    public ResponseEntity<List<CardReviewResponse>> getDueCardReviews(@PathVariable String userId, HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
        if (!userId.equals(user.getId().toHexString())) {
            throw new RuntimeException("Unauthorized access");
        }

        ObjectId userIdObj = new ObjectId(userId);
        List<CardReview> cardReviews = cardReviewService.getDueCardReviews(userIdObj);
        if (cardReviews != null && !cardReviews.isEmpty()) {
            List<CardReviewResponse> cardReviewResponses = cardReviews.stream()
                    .map(CardReviewMapping::entityToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(cardReviewResponses);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/review")
    public ResponseEntity<CardReviewResponse> reviewCard(@RequestParam String userId,
                                                         @RequestParam String cardId,
                                                         @RequestParam boolean isCorrect,
                                                         HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
        if (!userId.equals(user.getId().toHexString())) {
            throw new RuntimeException("Unauthorized access");
        }

        ObjectId userIdObj = new ObjectId(userId);
        ObjectId cardIdObj = new ObjectId(cardId);
        CardReview review = cardReviewService.updateReview(userIdObj, cardIdObj, isCorrect);
        if (review != null) {
            CardReviewResponse cardReviewResponse = CardReviewMapping.entityToResponse(review);
            return ResponseEntity.ok(cardReviewResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
