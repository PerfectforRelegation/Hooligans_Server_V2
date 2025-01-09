package com.joh.core.user.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "users") // MongoDB 컬렉션 이름
public class User {

  @Id
  private String id; // MongoDB ObjectId와 매핑

  private String oauthProvider;
  private String oauthId;
  private String email;
  private String nickname;
  private String profileImage;
  private LocalDateTime registrationDate;

  public User() {}

  @Builder
  public User(String oauthProvider, String oauthId, String email, String nickname,
      String profileImage, LocalDateTime registrationDate) {
    this.oauthProvider = oauthProvider;
    this.oauthId = oauthId;
    this.email = email;
    this.nickname = nickname;
    this.profileImage = profileImage;
    this.registrationDate = registrationDate;
  }
}
