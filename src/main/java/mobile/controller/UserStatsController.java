package mobile.controller;

import mobile.Service.UserService;
import mobile.Service.UserStatsService;
import mobile.model.Entity.User;
import mobile.model.Entity.UserStats;
import mobile.model.payload.request.user.AddDiamondRequest;
import mobile.model.payload.request.user.UpgradePremiumRequest;
import mobile.model.payload.request.user.UserStatsRequest;
import mobile.model.payload.response.user.UserFullInfoResponse;
import mobile.model.payload.response.user.UserStatsResponse;
import mobile.security.JWT.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user-stats")
public class UserStatsController {

    @Autowired
    private UserStatsService userStatsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/add-xp")
    public ResponseEntity<UserStats> addXp(@RequestBody UserStatsRequest userStatsRequest, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }

        String userId = userStatsRequest.getUserId();
        int xp = userStatsRequest.getXp();

        User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(accessToken));
        if (!userId.equals(user.getId().toHexString())) {
            throw new RuntimeException("Unauthorized access");
        }

        UserStats updatedStats = userStatsService.addXp(new ObjectId(userId), xp);
        return ResponseEntity.ok(updatedStats);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserStatsResponse> getStats(@PathVariable String userId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String accessToken = authHeader.substring("Bearer ".length());
        if (jwtUtils.validateExpiredToken(accessToken)) {
            throw new RuntimeException("Token expired");
        }
        return ResponseEntity.ok(userStatsService.getStatsByUserId(new ObjectId(userId)));
    }

    @GetMapping("/top-users")
    public ResponseEntity<Page<UserFullInfoResponse>> getTopUsersWithStats(@RequestParam(defaultValue = "10") int limit) {
        Page<UserFullInfoResponse> topUsers = userStatsService.getTopUsersWithStats(limit);

        return ResponseEntity.ok(topUsers);
    }

    @PostMapping("/add-diamond")
    public ResponseEntity<UserStats> addDiamonds(@RequestBody AddDiamondRequest addDiamondRequest, HttpServletRequest request) {

        ObjectId userId = new ObjectId(addDiamondRequest.getUserId());
        int diamond = addDiamondRequest.getDiamond();
        UserStats userStats = userStatsService.addDiamond(userId, diamond);
        return ResponseEntity.ok(userStats);
    }

    @PostMapping("/upgrade-premium")
    public ResponseEntity<UserStats> upgradePremium(@RequestBody UpgradePremiumRequest upgradePremiumRequest, HttpServletRequest request) {
        // Validate JWT token
        ObjectId userId = new ObjectId(upgradePremiumRequest.getUserId());
        int days = upgradePremiumRequest.getDays();
        int cost = upgradePremiumRequest.getCost();
        return ResponseEntity.ok(userStatsService.upgradePremium(userId, days, cost));
    }
}
