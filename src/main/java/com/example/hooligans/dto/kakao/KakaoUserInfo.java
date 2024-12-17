package com.example.hooligans.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfo {

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class KakaoAccount {
    private String email;
    private Profile profile;

    public KakaoAccount(String email, Profile profile) {
      this.email = email;
      this.profile = profile;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
      private String nickname;

      public Profile(String nickname) {
        this.nickname = nickname;
      }
    }
  }

  private Long id;

  @JsonProperty("kakao_account")
  private KakaoAccount kakaoAccount;

  public KakaoUserInfo(Long id, KakaoAccount kakaoAccount) {
    this.id = id;
    this.kakaoAccount = kakaoAccount;
  }
}
