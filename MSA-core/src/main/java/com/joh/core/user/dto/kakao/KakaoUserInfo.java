package com.joh.core.user.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfo {

  private Long id;

  @JsonProperty("kakao_account")
  private KakaoAccount kakaoAccount;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class KakaoAccount {

    private String email;
    private Profile profile;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {

      private String nickname;
    }
  }
}
