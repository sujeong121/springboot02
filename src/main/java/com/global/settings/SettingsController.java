package com.global.settings;

import com.global.account.AccountService;
import com.global.account.CurrentUser;
import com.global.domain.Account;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


// error 없을 시 update(수정) 진행
// data 수정 시엔 Service 에 위임해서
//         ㄴ (Service 클래스 타입의 멤버변수를 선언해야 함)
//         멤버변수를 parameter 하는 생성자를 작성해서 Service 객체를 주입 받음
//         ㄴ @RequiredArgsConstructor
//
//
//
// Transaction 안에서 수정해야
// DB 에 반영됨  <-- @Transaction


@Controller
@RequiredArgsConstructor
public class SettingsController {

  static final String SETTINGS_PASSWORD_VIEW = "settings/password";
  static final String SETTINGS_PASSWORD_URL = "/settings/password";

  // PasswordFormValidator 를 Bean 으로 등록하지 않고
  // initBinder 를 사용해서 객체를 생성
  @InitBinder("passwordForm")
  public void initBinder(WebDataBinder webDataBinder){
    webDataBinder.addValidators(new PasswordFormValidator());
  }

  // settings/profile 문자열을 static 변수에 저장
  static final String SETTINGS_PROFILE_VIEW = "settings/profile";
  static final String SETTINGS_PROFILE_URL = "/settings/profile";

  static final String SETTINGS_NOTIFICATIONS_VIEW = "settings/notifications";
  static final String SETTINGS_NOTIFICATIONS_URL = "/settings/notifications";

  //
  private final AccountService accountService;

  // 주소표시줄에 /settings/profile 요청이 들어오면
  // 자동으로 호출되는 메소드
  // @CurrentUser <-- 현재 user(현재 로그인 상태인 회원)
  //      정보를 가져오기 위한 Annotation
  @GetMapping(SETTINGS_PROFILE_URL)
  public String updateProfileForm(@CurrentUser Account account, Model model){
    // model.addAttribute("account", account); 아래코드랑 같은 기능
    model.addAttribute(account);
    // model.addAttribute("profile", new Profile(account));
    model.addAttribute(new Profile(account));

    return SETTINGS_PROFILE_VIEW;
  }

  // post 방식으로 요청이 들어올 때
  // 자동으로 호출되는 메소드
  // @CurrentUser <-- 현재 user(현재 로그인 상태인 회원)
  //      정보를 가져오기 위한 Annotation
  // @Valid @ModelAttribute Profile profile
  //  ㄴ form 에서 입력한 값들은 @ModelAttribute 를 사용해서 Profile 객체로 받아옴
  //  ㄴ @ModelAttribute 생략 가능         ㄴ @ModelAttribute 로 data 를 biding 함
  // Errors errors : @ModelAttribute 객체의 biding error 를 받아주는 객체
  //  ㄴ
  //
  //
  //
  @PostMapping(SETTINGS_PROFILE_URL)
  public String updateProfile(@CurrentUser Account account,
                              @Valid @ModelAttribute Profile profile,
                              Errors errors, Model model, RedirectAttributes redirectAttributes){



    // error 가 있는 경우 (Validation 위반)
    //  ㄴ model 에 form 에 채워진 data 가 자동으로 들어가고,
    //   error 에 대한 정보도 model 에 자동으로 들어감
    //
    if(errors.hasErrors()){
      model.addAttribute(account);
      // 화면에서는 현재 view 를 그대로 보여줌
      return SETTINGS_PROFILE_VIEW;
    }

    // error 없을 시 update(수정) 진행
    // data 수정 시엔 Service 에 위임해서
    // Transaction 안에서 수정해야
    // DB 에 반영됨  <-- @Transaction
    accountService.updateProfile(account, profile);
    redirectAttributes.addFlashAttribute("message", "프로필이 수정되었습니다.");

    //
    return "redirect:" + SETTINGS_PROFILE_URL;
  }

  @GetMapping(SETTINGS_PASSWORD_URL)
  public String passwordUpdateForm(@CurrentUser Account account, Model model){
    model.addAttribute(account);

    // Form 으로 사용할 객체 없음 -> Form 으로 사용할 클래스 작성
    //
    model.addAttribute(new PasswordForm());

    return SETTINGS_PASSWORD_VIEW;
  }

  @PostMapping(SETTINGS_PASSWORD_URL)
  public String updatePassword(@CurrentUser Account account,
                               @Valid PasswordForm passwordForm,
                               Errors errors, Model model,
                               RedirectAttributes redirectAttributes){
    if(errors.hasErrors()){
      model.addAttribute(account);
      return SETTINGS_PASSWORD_VIEW;
    }

    accountService.updatePassword(account, passwordForm.getNewPassword());
    redirectAttributes.addFlashAttribute("message", "비밀번호를 수정했습니다.");

    return "redirect:" + SETTINGS_PASSWORD_URL;
  }

  @GetMapping(SETTINGS_NOTIFICATIONS_URL)
  public String updateNotificationsForm(@CurrentUser Account account, Model model){
    model.addAttribute(account);
    model.addAttribute(new Notifications(account));
    return SETTINGS_NOTIFICATIONS_VIEW;
  }
  @PostMapping(SETTINGS_NOTIFICATIONS_URL)
  public String updateNotifications(@CurrentUser Account account,
                                    @Valid Notifications notifications,
                                    Errors errors, Model model,
                                    RedirectAttributes redirectAttributes){
    if(errors.hasErrors()){
      model.addAttribute(account);
      return SETTINGS_NOTIFICATIONS_VIEW;
    }

    accountService.updateNotifications(account, notifications);
    redirectAttributes.addFlashAttribute("message", "알림 설정이 변경되었습니다.");
    return "redirect:" + SETTINGS_NOTIFICATIONS_URL;
  }
}
