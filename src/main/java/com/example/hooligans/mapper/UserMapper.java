package com.example.hooligans.mapper;

import com.example.hooligans.dto.UserResponse;
import com.example.hooligans.entity.User;
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
