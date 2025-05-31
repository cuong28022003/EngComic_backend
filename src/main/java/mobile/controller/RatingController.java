package mobile.controller;

import mobile.Handler.RecordNotFoundException;
import mobile.Service.*;
import mobile.mapping.RatingMapping;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Rating;
import mobile.model.Entity.User;
import mobile.model.payload.request.rating.RatingRequest;
import mobile.model.payload.response.RatingResponse;
import mobile.security.JWT.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/rating")
public class RatingController {

    @Autowired
    UserService userService;

    @Autowired
    ComicService comicService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RatingService ratingService;

    @GetMapping("/comic/{comicId}")
    public ResponseEntity<Page<RatingResponse>> getRatingsForComic(
            @PathVariable String comicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<RatingResponse> ratings = ratingService.getRatingsForComic(new ObjectId(comicId), PageRequest.of(page, size));
        return ResponseEntity.ok(ratings);
    }

    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(@RequestBody RatingRequest ratingRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        Rating rating = RatingMapping.requestToEntity(ratingRequest);

        return ResponseEntity.ok(ratingService.createOrUpdateRating(rating));
    }

    @GetMapping("/summary/{comicId}")
    public Map<String, Object> getRatingSummary(@PathVariable String comicId) {
        List<Rating> ratings = ratingService.getRatingsByComicId(new ObjectId(comicId));
        double avg = ratings.stream().mapToInt(Rating::getRating).average().orElse(0.0);
        int total = ratings.size();

        Map<String, Object> result = new HashMap<>();
        result.put("averageRating", avg);
        result.put("totalRatings", total);
        return result;
    }
}
