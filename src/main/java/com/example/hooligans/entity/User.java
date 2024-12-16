package com.example.hooligans.entity;

import com.example.hooligans.entity.utils.OAuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "oauth_provider" ,nullable = false)
  private OAuthProvider oAuthProvider;

  @Column(name = "oauth_id", nullable = false)
  private String oAuthId;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String nickname;

  @Column(name = "profile_image")
  private String profileImage;

  @Column(nullable = false)
  private LocalDateTime registrationDate;

  public User() {}
}
