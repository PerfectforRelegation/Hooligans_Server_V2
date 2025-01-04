package com.joh.epl.mapper;

import com.joh.epl.dto.UserResponse;
import com.joh.epl.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserResponse toUserResponse(User user) {

    return UserResponse.builder()
        .email(user.getEmail())
        .nickname(user.getNickname())
        .profileImage(user.getProfileImage())
        .build();
  }
}
