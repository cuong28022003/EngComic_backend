package mobile.controller;

import mobile.Handler.RecordNotFoundException;
import mobile.Service.*;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Rating;
import mobile.model.Entity.User;
import mobile.model.payload.request.rating.RatingRequest;
import mobile.model.payload.response.RatingResponse;
import mobile.security.JWT.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/comics")
public class RatingController {

    @Autowired
    UserService userService;

    @Autowired
    ComicService comicService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RatingService ratingService;

    @PostMapping("/{url}/rate")
    public ResponseEntity<RatingResponse> rateComic( @PathVariable String url, @RequestBody RatingRequest ratingRequest, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken)) {
                throw new BadCredentialsException("Access token đã hết hạn");
            }

            String username = jwtUtils.getUserNameFromJwtToken(accessToken);
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new RecordNotFoundException("Không tìm thấy người dùng");
            }

            Comic comic = comicService.findByUrl(url);
            if (comic == null) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }

            Rating userRating = ratingService.rateComic(comic, user, ratingRequest.getRating());

            double averageRating = ratingService.calculateAverageRating(comic);
            int ratingCount = ratingService.getTotalReviews(comic);
            comic.setRating(averageRating);
            comic.setRatingCount(ratingCount);
            comicService.SaveComic(comic);

            RatingResponse response = new RatingResponse(
                    userRating.getRating(),
                    ratingService.calculateAverageRating(comic),
                    ratingService.getTotalReviews(comic)
            );

            return ResponseEntity.ok(response);
        }else {
            throw new BadCredentialsException("Không tìm thấy Access Token");
        }
    }

    @PostMapping("/{url}/rating")
    public ResponseEntity<RatingResponse> getComicRating( @PathVariable String url, HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken)) {
                throw new BadCredentialsException("Access token đã hết hạn");
            }

            String username = jwtUtils.getUserNameFromJwtToken(accessToken);
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new RecordNotFoundException("Không tìm thấy người dùng");
            }

            Comic comic = comicService.findByUrl(url);
            if (comic == null) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }

            int userRating = 0;
            Rating existingRating = ratingService.getRatingByComicAndUser(comic, user);
            if (existingRating != null) {
                userRating = existingRating.getRating();
            }

            RatingResponse response = new RatingResponse(
                    userRating,
                    ratingService.calculateAverageRating(comic),
                    ratingService.getTotalReviews(comic)
            );

            return ResponseEntity.ok(response);
        }else {
            throw new BadCredentialsException("Không tìm thấy Access Token");
        }
    }

    @DeleteMapping("/{url}/rating")
    public ResponseEntity<String> deleteRating(
            @PathVariable String url, HttpServletRequest request) {

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken)) {
                throw new BadCredentialsException("Access token đã hết hạn");
            }

            String username = jwtUtils.getUserNameFromJwtToken(accessToken);
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new RecordNotFoundException("Không tìm thấy người dùng");
            }

            Comic comic = comicService.findByUrl(url);
            if (comic == null) {
                throw new RecordNotFoundException("Không tìm thấy truyện");
            }

            ratingService.deleteRating(comic, user);
            return ResponseEntity.ok("Rating deleted successfully");

        }else {
            throw new BadCredentialsException("Không tìm thấy Access Token");
        }
    }
}
