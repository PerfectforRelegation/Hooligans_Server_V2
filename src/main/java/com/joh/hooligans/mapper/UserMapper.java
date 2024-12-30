package com.joh.hooligans.mapper;

import com.joh.hooligans.dto.UserResponse;
import com.joh.hooligans.entity.User;
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
