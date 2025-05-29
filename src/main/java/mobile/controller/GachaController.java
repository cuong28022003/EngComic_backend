package mobile.controller;

import mobile.Service.GachaService;
import mobile.Service.UserService;
import mobile.model.Entity.User;
import mobile.model.payload.response.character.CharacterResponse;
import mobile.model.payload.response.pack.GachaPackResult;
import mobile.security.JWT.JwtUtils;
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

    @PostMapping("/roll")
    public ResponseEntity<List<CharacterResponse>> roll(@RequestParam(defaultValue = "1") int count, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));


        return ResponseEntity.ok(gachaService.roll(user.getId(), count));
    }
}