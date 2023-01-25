package com.global.settings;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormValidator implements Validator {

  // 어떤 type 의 form 객체를 검증하는가?
  // new 로 생성하든지 SettingsController 에서 @InitBinder()로 처리
  // 여기서는 SettingsController 에서 @InitBinder()로 처리
  @Override
  public boolean supports(Class<?> clazz) {
    // PasswordForm type 에 할당 가능한 type 이어야만 검증
    return PasswordForm.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    // PasswordForm 으로 형변환해서 이 둘이 같은지 비교
    PasswordForm passwordForm = (PasswordForm)target;

    // password 다른 경우
    if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())){
      errors.rejectValue("newPassword", "wrong.value", "입력하신 비밀번호가 일치하지 않습니다");
    }
  }
}
