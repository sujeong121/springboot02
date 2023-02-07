package com.global.study;

import com.global.domain.Account;
import com.global.domain.Study;
import com.global.domain.Tag;
import com.global.domain.Zone;
import com.global.study.form.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {
  private final StudyRepository studyRepository;
  private final ModelMapper modelMapper;
  private final ApplicationEventPublisher eventPublisher;

  public Study createNewStudy(Study study, Account account) {
    // study 를 studyRepository 에 저장하고
    // 이 때 생성된 study 객체를 newStudy 변수에 연결
    Study newStudy = studyRepository.save(study);

    // newStudy 에 manager 로 account 추가
    newStudy.addManager(account);

    return newStudy;
  }

  public Study getStudyToUpdate(Account account, String path) {
    Study study = studyRepository.findStudyWithManagersByPath(path);
    checkIfExistingStudy(path, study);
    // manager 가 아니면 study 정보를 수정할 수 없음
    checkifManager(account, study);
    return study;
  }

  private void checkifManager(Account account, Study study){
    if(!account.isManagerOf(study)){
      throw new AccessDeniedException("현재 사용자는 수정 기능을 사용할 수 없습니다.");
    }
  }

  public Study getStudy(String path){
    Study study = this.studyRepository.findByPath(path);
    checkIfExistingStudy(path, study);
    return study;
  }

  private void checkIfExistingStudy(String path, Study study){
    if (study == null){
      throw new IllegalArgumentException(path + "란 이름으로 개설된 study 가 없습니다");
    }
  }

  public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
    modelMapper.map(studyDescriptionForm, study);
  }

  public void updateStudyImage(Study study, String image) {
    study.setImage(image);
  }

  public void enableStudyBanner(Study study) {
    study.setUseBanner(true);
  }

  public void disableStudyBanner(Study study) {
    study.setUseBanner(false);
  }

  // StudySettingsController 의 public ResponseEntity addTag(study, tag) 메소드에서 호출함
  // addTag() 호출하면서 전달한 study, tag 객체를 persistence 객체라서
  // study.getTags().add(tag);  <-- 이 부분이 실행될 때 이 값들의 상태가 DB 에도 반영됨
  public void addTag(Study study, Tag tag) {
    // Study 클래스의 tags 는 HashSet 임
    // study.getTags() : Study 클래스의 tags 에
    //   .add(tag) : Tag 객체 추가함
    study.getTags().add(tag);
  }

  public void removeTag(Study study, Tag tag) {
    study.getTags().remove(tag);
  }

  public void addZone(Study study, Zone zone) {
    study.getZones().add(zone);
  }

  public void removeZone(Study study, Zone zone) {
    study.getZones().remove(zone);
  }

  public Study getStudyToUpdateStatus(Account account, String path) {
    Study study = studyRepository.findStudyWithManagersByPath(path);
    checkIfExistingStudy(path, study);
    checkifManager(account, study);
    return study;
  }

  public void publish(Study study) {
    study.publish();
  }

  public void close(Study study) {
    study.close();
  }

  public void startRecruit(Study study) {
    study.canUpdateRecruiting();
  }

  public void stopRecruit(Study study) {
    //study.stopRecruit()
  }
}
