package com.joh.core.club.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/club")
public class ClubController {

  @GetMapping("/test")
  public String test() {
    return "club test";
  }
}
