package com.joh.hooligans.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table(name = "users")
public class User {

  @Id
  private Long id;

  @Column("oauth_provider")
  private String oauthProvider;

  @Column("oauth_id")
  private String oauthId;

  @Column("email")
  private String email;

  @Column("nickname")
  private String nickname;

  @Column("profile_image")
  private String profileImage;

  @Column("registration_date")
  private LocalDateTime registrationDate;

  public User() {}

  @Builder
  public User(String oauthProvider, String oauthId, String email, String nickname,
      LocalDateTime registrationDate) {
    this.oauthProvider = oauthProvider;
    this.oauthId = oauthId;
    this.email = email;
    this.nickname = nickname;
    this.registrationDate = registrationDate;
  }
}
