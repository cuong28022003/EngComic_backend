package mobile.mapping;

import mobile.model.Entity.User;
import mobile.model.Entity.UserStats;
import mobile.model.payload.request.user.InfoUserRequest;
import mobile.model.payload.request.user.RegisterAdminRequest;
import mobile.model.payload.request.authenticate.RegisterRequest;
import mobile.model.payload.response.user.UserStatsResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserMapping {

    public static User registerToEntity(RegisterRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return new User(registerRequest.getUsername(),registerRequest.getEmail(),registerRequest.getPassword());
    }

    public static User registerToEntity(RegisterAdminRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return new User(registerRequest.getUsername(),registerRequest.getEmail(),registerRequest.getPassword());
    }

    public static User UpdateUserInfoByUser(User user, InfoUserRequest userInfo) {
        user.setBirthdate(userInfo.getBirthdate());
        user.setFullName(userInfo.getFullName());
        user.setImage(userInfo.getImage());
        return  user;
    }

    public static User UpdatePasswordByUser(User user, String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(password));
        return  user;
    }
public static UserStatsResponse mapToUserStatsResponse(User user, UserStats userStats) {
        return new UserStatsResponse(
            user.getId().toHexString(),
            user.getUsername(),
            user.getEmail(),
            user.getImage(),
            userStats.getXp(),
            userStats.getCurrentStreak(),
            userStats.getLongestStreak(),
            userStats.getRank().getName(),
            userStats.getRank().getBadge(),
            userStats.getLastStudyDate()
        );
    }

}
