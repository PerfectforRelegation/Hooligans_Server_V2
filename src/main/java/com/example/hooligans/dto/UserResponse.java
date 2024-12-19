package com.example.hooligans.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponse {

  private String email;
  private String nickname;
  private String profileImage;

  @Builder
  public UserResponse(String email, String nickname, String profileImage) {
    this.email = email;
    this.nickname = nickname;
    this.profileImage = profileImage;
  }
}
