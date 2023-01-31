package com.global.mail;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmailMessage {
  // 전송 대상자
  private String toWhom;
  // 제목
  private String subject;
  // 본문 내용
  private String message;
}
