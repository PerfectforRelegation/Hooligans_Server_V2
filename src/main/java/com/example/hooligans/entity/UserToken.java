package com.example.hooligans.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@Table("user_token")
public class UserToken {

  @Id
  private Long id;

  @Column("user_id")
  private Long userId;

  @Column("refresh_token")
  private String refreshToken;

  public UserToken(Long userId, String refreshToken) {
    this.userId = userId;
    this.refreshToken = refreshToken;
  }
}
