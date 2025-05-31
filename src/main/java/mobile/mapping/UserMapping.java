package mobile.mapping;

import lombok.RequiredArgsConstructor;
import mobile.Service.RankService;
import mobile.Service.UserService;
import mobile.model.Entity.Rank;
import mobile.model.Entity.User;
import mobile.model.Entity.UserStats;
import mobile.model.payload.request.user.InfoUserRequest;
import mobile.model.payload.request.user.RegisterAdminRequest;
import mobile.model.payload.request.authenticate.RegisterRequest;
import mobile.model.payload.response.rank.RankResponse;
import mobile.model.payload.response.user.UserFullInfoResponse;
import mobile.model.payload.response.user.UserResponse;
import mobile.model.payload.response.user.UserStatsResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapping {

    private final RankService rankService;

    public static User registerToEntity(RegisterRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return new User(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
    }

    public static User registerToEntity(RegisterAdminRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return new User(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
    }

    public static User UpdateUserInfoByUser(User user, InfoUserRequest userInfo) {
        user.setBirthday(userInfo.getBirthday());
        user.setFullName(userInfo.getFullName());
        user.setImage(userInfo.getImage());
        return user;
    }

    public static User UpdatePasswordByUser(User user, String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

    public UserStatsResponse mapToUserStatsResponse(UserStats userStats) {

        return new UserStatsResponse(
                userStats.getId().toHexString(),
                userStats.getUserId().toHexString(),
                userStats.getXp(),
                userStats.getDiamond(),
                rankService.getRankByXp(userStats.getXp()),
                userStats.getCurrentStreak(),
                userStats.getLongestStreak(),
                userStats.getLastStudyDate(),
                userStats.isReceivedSeasonReward(),
                userStats.isPremium(),
                userStats.getPremiumExpiredAt()
        );
    }

    public UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId().toHexString(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getImage(),
                user.getBirthday()
        );
    }

    public UserFullInfoResponse toUserFullInfoResponse(User user, UserStats userStats) {
        UserResponse userResponse = mapToUserResponse(user);
        UserStatsResponse userStatsResponse = mapToUserStatsResponse(userStats);
        return new UserFullInfoResponse(userResponse, userStatsResponse);
    }

}
