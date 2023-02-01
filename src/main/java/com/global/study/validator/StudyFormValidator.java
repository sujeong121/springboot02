package com.global.study.validator;

import com.global.study.StudyRepository;
import com.global.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {

  private final StudyRepository studyRepository;

  @Override
  public boolean supports(Class<?> clazz) {
    return StudyForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    StudyForm studyForm = (StudyForm)target;
    // Path 확인
    if(studyRepository.existsByPath(studyForm.getPath())){
      errors.rejectValue("path", "wrong.path", "입력하신 경로를 사용할 수 없습니다.");
    }
  }
}
