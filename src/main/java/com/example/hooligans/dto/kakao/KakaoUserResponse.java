package com.example.hooligans.dto.kakao;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserResponse {

  private Long id;
  private String nickname;
  private String email;

  @Builder
  public KakaoUserResponse(Long id, String nickname, String email) {
    this.id = id;
    this.nickname = nickname;
    this.email = email;
  }
}
