package com.example.hooligans.dto.kakao;

import com.example.hooligans.entity.User;
import com.example.hooligans.entity.utils.OAuthProvider;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialUserRequest {

  private Long socialId;
  private String nickname;
  private String email;

  @Builder
  public SocialUserRequest(Long socialId, String nickname, String email) {
    this.socialId = socialId;
    this.nickname = nickname;
    this.email = email;
  }

  public User toEntity() {

    return User.builder()
        .oauthProvider(OAuthProvider.KAKAO.name())
        .oauthId(createOAuthId())
        .email(this.email)
        .nickname(this.nickname)
        .registrationDate(LocalDateTime.now())
        .build();
  }

  private String createOAuthId() {
    return OAuthProvider.KAKAO + "_" + this.socialId;
  }
}
