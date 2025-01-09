package com.joh.core.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

  @GetMapping("/notification/test")
  public String test() {
    return "notification test";
  }
}
