package mobile.controller;

import mobile.Service.GachaService;
import mobile.Service.UserService;
import mobile.model.Entity.Character;
import mobile.model.Entity.User;
import mobile.model.payload.request.gacha.OpenGachaRequest;
import mobile.model.payload.response.card.GachaPackResult;
import mobile.security.JWT.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/gacha")
public class GachaController {

    @Autowired
    private GachaService gachaService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

//    @PostMapping("/open")
//    public ResponseEntity<Character> openGacha(@RequestBody OpenGachaRequest openGachaRequest, HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new RuntimeException("Invalid token");
//        }
//        String accessToken = authHeader.substring("Bearer ".length());
//        if (jwtUtils.validateExpiredToken(accessToken)) {
//            throw new RuntimeException("Token expired");
//        }
//        String userId = openGachaRequest.getUserId();
//        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
//        if (!userId.equals(user.getId().toHexString())) {
//            throw new RuntimeException("Unauthorized access");
//        }
//
//        ObjectId uid = new ObjectId(userId);
//        Character receivedCard = gachaService.openGacha(uid);
//        return ResponseEntity.ok(receivedCard);
//    }

    @PostMapping("/roll")
    public ResponseEntity<List<GachaPackResult>> roll(@RequestParam(defaultValue = "1") int count) {
        return ResponseEntity.ok(gachaService.roll(count));
    }
}