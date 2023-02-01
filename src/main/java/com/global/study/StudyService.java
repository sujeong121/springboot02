package com.global.study;

import com.global.domain.Account;
import com.global.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {
  private final StudyRepository studyRepository;

  public Study createNewStudy(Study study, Account account) {
    // study 를 studyRepository 에 저장하고
    // 이 때 생성된 study 객체를 newStudy 변수에 연결
    Study newStudy = studyRepository.save(study);

    // newStudy 에 manager 로 account 추가
    newStudy.addManager(account);

    return newStudy;
  }
}
