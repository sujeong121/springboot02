package com.global.settings;

import com.global.account.AccountService;
import com.global.account.CurrentUser;
import com.global.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
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

  // settings/profile 문자열을 static 변수에 저장
  static final String SETTINGS_PROFILE_VIEW = "settings/profile";
  static final String SETTINGS_PROFILE_URL = "/settings/profile";

  //
  private final AccountService accountService;

  // 주소표시줄에 /settings/profile 요청이 들어오면
  // 자동으로 호출되는 메소드
  // @CurrentUser <-- 현재 user(현재 로그인 상태인 회원)
  //      정보를 가져오기 위한 Annotation
  @GetMapping("/settings/profile")
  public String profileUpdateForm(@CurrentUser Account account, Model model){
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
  @PostMapping("settings/profile")
  public String updateProfile(@CurrentUser Account account,
                              @Valid @ModelAttribute Profile profile,
                              Errors errors, Model model, RedirectAttributes redirectAttributes){



    // error 가 있는 경우 (Validation 위반)
    //  ㄴ model 에 form 에 채워진 data 가 자동으로 들어가고,
    //   error 에 대한 정보도 model 에 자동으로 들어감
    //
    if(errors.hasErrors()){
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
}
