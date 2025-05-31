package mobile.Service;

import mobile.model.Entity.Role;
import mobile.model.Entity.User;
import mobile.model.payload.request.user.InfoUserRequest;
import mobile.model.payload.response.user.UserResponse;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface UserService {
    User saveUser(User user, String roleName);
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String email, String roleName);
    void updateRoleToUser(User user, List<String> roleList);
    User getUser(String email);
    List<User> getUsers();
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    User findByUsername(String username);
    User updateUserInfo(ObjectId userId, String fullName, LocalDate birthday, MultipartFile image);
    User updateUserPassword(User user, String password);
    User deleteUser(String username);
    User updateActive(User user);
    UserResponse findById(ObjectId id);
}
