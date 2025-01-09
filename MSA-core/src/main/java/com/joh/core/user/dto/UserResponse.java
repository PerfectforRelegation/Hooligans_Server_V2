package com.joh.core.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponse {

  private String email;
  private String nickname;
  private String profileImage;
  private String accessToken;

  @Builder
  public UserResponse(String email, String nickname, String profileImage, String accessToken) {
    this.email = email;
    this.nickname = nickname;
    this.profileImage = profileImage;
    this.accessToken = accessToken;
  }
}
