package mobile.businesses.interactors.user;

import mobile.apis.user.dtos.UserProfileDto;
import mobile.databases.entities.user.RoleEntity;
import mobile.databases.entities.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserProfileDto toProfileDto(UserEntity user) {
        if (user == null) {
            return null;
        }

        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .birthday(user.getBirthday())
                .createdDate(user.getCreatedate())
                .imageUrl(user.getImage())
                .active(user.getActive())
                .status(user.getStatus())
                .roles(user.getRoles() != null
                        ? user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet())
                        : Collections.emptySet())
                .build();
    }
}
