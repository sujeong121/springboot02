package com.global.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService{
  // JavaMailSender 주입받기
  private final JavaMailSender javaMailSender;

  @Override
  public void sendEmail(EmailMessage emailMessage) {
    /* AccountService 클래스의 sendSignUpConfirmEmail(Account newAccount) 에서 작성한 부분을 가져옴 */
    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    try {
      MimeMessageHelper mimeMessageHelper =
        new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(emailMessage.getToWhom());
      mimeMessageHelper.setSubject(emailMessage.getSubject());
      mimeMessageHelper.setText(emailMessage.getMessage(), true);
      javaMailSender.send(mimeMessage);
      log.info("sent email : {}", emailMessage.getMessage());
    } catch (MessagingException e) {
      // Exception 발생하면 log 에 기록해서 출력하기
      // AccountService 클래스에 @Slf4j
      log.error("메일 전송 오류 발생", e);
      //
      throw new RuntimeException(e);
    }

  }
}
