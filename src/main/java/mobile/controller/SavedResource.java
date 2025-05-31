package mobile.controller;


import lombok.RequiredArgsConstructor;
import mobile.Handler.HttpMessageNotReadableException;
import mobile.Handler.RecordNotFoundException;
import mobile.Service.ComicService;
import mobile.Service.SavedService;
import mobile.Service.UserService;
import mobile.mapping.SavedMapping;
import mobile.model.Entity.Comic;
import mobile.model.Entity.Saved;
import mobile.model.Entity.User;
import mobile.model.payload.request.saved.SavedRequest;
import mobile.model.payload.response.SavedResponse;
import mobile.model.payload.response.SuccessResponse;
import mobile.security.JWT.JwtUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("api/saved")
@RequiredArgsConstructor
public class SavedResource {
    private static final Logger LOGGER = LogManager.getLogger(ComicController.class);

    private final UserService userService;
    private final ComicService comicService;
    private final SavedService savedService;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("")
    public ResponseEntity<SavedResponse> createSaved(@RequestBody SavedRequest savedRequest
                                                        , HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        ObjectId userId = new ObjectId(savedRequest.getUserId());
        ObjectId comicId = new ObjectId(savedRequest.getComicId());
        SavedResponse savedResponse = savedService.create(userId, comicId);
        return ResponseEntity.ok(savedResponse);
    }



    @GetMapping("/user/{userId}")
    @ResponseBody
    public ResponseEntity<Page<SavedResponse>> getSavedByUserId(@PathVariable String userId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        Page<SavedResponse> savedResponses = savedService.getByUserId(new ObjectId(userId), PageRequest.of(page, size));
        return ResponseEntity.ok(savedResponses);

    }

    @GetMapping("/{id}")
    @ResponseBody
    public SavedResponse checkSavedByUsername(@PathVariable String id,HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        return savedService.getById(new ObjectId(id));

    }
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSaved(@PathVariable String id
            , HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        savedService.delete(new ObjectId(id));
        return ResponseEntity.ok("Saved novel deleted successfully");
    }

    @GetMapping("/check")
    public ResponseEntity<SavedResponse> checkSavedByUserIdAndComicId(@RequestParam String userId, @RequestParam String comicId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        SavedResponse savedResponse = savedService.getByUserIdAndComicId(new ObjectId(userId), new ObjectId(comicId));
        return ResponseEntity.ok(savedResponse);
    }

}
