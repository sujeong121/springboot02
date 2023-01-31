package com.global.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local")
@Component
@Slf4j
public class ConsoleEmailService implements EmailService{
  @Override
  public void sendEmail(EmailMessage emailMessage) {
    log.info("메일 전송 : {}", emailMessage.getMessage());
  }
}
