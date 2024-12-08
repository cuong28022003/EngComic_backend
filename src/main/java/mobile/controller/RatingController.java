//package mobile.controller;
//
//import mobile.Handler.RecordNotFoundException;
//import mobile.Service.*;
//import mobile.model.Entity.Comic;
//import mobile.model.Entity.Rating;
//import mobile.model.Entity.User;
//import mobile.model.payload.request.rating.RatingRequest;
//import mobile.repository.RatingRepository;
//import mobile.security.JWT.JwtUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//
//import static com.google.common.net.HttpHeaders.AUTHORIZATION;
//
//@RestController
//@RequestMapping("/api/rating")
//public class RatingController {
//
//    @Autowired
//    UserService userService;
//
//    @Autowired
//    JwtUtils jwtUtils;
//
//    @Autowired
//    private RatingRepository ratingRepository;
//
//    // Create a new rating
//    public Rating createRating(@RequestParam("url") String url, HttpServletRequest request) {
//        String authorizationHeader = request.getHeader(AUTHORIZATION);
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            String accessToken = authorizationHeader.substring("Bearer ".length());
//
//            // Kiểm tra xem token có hết hạn không
//            if (jwtUtils.validateExpiredToken(accessToken)) {
//                throw new BadCredentialsException("Access token đã hết hạn");
//            }
//
//            // Lấy thông tin người dùng từ token
//            String username = jwtUtils.getUserNameFromJwtToken(accessToken);
//            User user = userService.findByUsername(username);
//            if (user == null) {
//                throw new RecordNotFoundException("Không tìm thấy người dùng");
//            }
//
//
//        Rating rating = new Rating();
//        rating.setUser(new User(ratingRequest.getUserId()));
//        rating.setComic(new Comic(ratingRequest.getComicId()));
//        rating.setRating(ratingRequest.getRating());
//        return ratingRepository.save(rating);
//    }
//
//    // Get all ratings
//    public List<Rating> getAllRatings() {
//        return ratingRepository.findAll();
//    }
//
//    // Get a rating by ID
//    public Rating getRatingById(String id) {
//        Optional<Rating> rating = ratingRepository.findById(new ObjectId(id));
//        return rating.orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));
//    }
//
//    // Update a rating
//    public Rating updateRating(String id, RatingRequest ratingRequest) {
//        Rating rating = getRatingById(id);
//        rating.setUser(new User(ratingRequest.getUserId()));
//        rating.setComic(new Comic(ratingRequest.getComicId()));
//        rating.setRating(ratingRequest.getRating());
//        return ratingRepository.save(rating);
//    }
//
//    // Delete a rating
//    public void deleteRating(String id) {
//        Rating rating = getRatingById(id);
//        ratingRepository.delete(rating);
//    }
//}
