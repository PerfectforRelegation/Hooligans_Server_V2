package com.joh.core.user.mapper;

import com.joh.core.user.dto.UserResponse;
import com.joh.core.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserResponse toUserResponse(User user, String accessToken) {

    return UserResponse.builder()
        .email(user.getEmail())
        .nickname(user.getNickname())
        .profileImage(user.getProfileImage())
        .accessToken(accessToken)
        .build();
  }
}
