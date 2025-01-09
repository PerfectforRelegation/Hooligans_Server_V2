package com.joh.core.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginCheckDTO {

  private boolean isSignUp;
  private UserResponse userResponse;

  @Builder
  public LoginCheckDTO(boolean isSignUp, UserResponse userResponse) {
    this.isSignUp = isSignUp;
    this.userResponse = userResponse;
  }
}
