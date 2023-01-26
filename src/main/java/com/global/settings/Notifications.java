package com.global.settings;

import com.global.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
// @NoArgsConstructor
public class Notifications {

  private boolean studyCreatedByEmail;
  private boolean studyCreatedByWeb;
  private boolean studyEnrollmentResultByEmail;
  private boolean studyEnrollmentResultByWeb;
  private boolean studyUpdatedByEmail;
  private boolean studyUpdatedByWeb;
/*
  public Notifications(Account account) {
    // Notifications 클래스는 Bean 이 아니라
    // ModelMapper 주입 못받음
    //  ㄴ 명시적(코딩)으로 객체를 생성해줘야 함
    // ModelMapper modelMapper = new ModelMapper();

    // modelMapper.map(this, account);

    this.studyCreatedByEmail = account.isStudyCreatedByEmail();
    this.studyCreatedByWeb = account.isStudyCreatedByWeb();
    this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
    this.studyEnrollmentResultByWeb = account.isStudyEnrollmentResultByWeb();
    this.studyUpdatedByEmail = account.isStudyUpdatedByEmail();
    this.studyUpdatedByWeb = account.isStudyUpdatedByWeb();
  }
 */
}
