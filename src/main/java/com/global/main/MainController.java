package com.global.main;

import com.global.account.CurrentUser;
import com.global.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
  @GetMapping("/")
  public String home(@CurrentUser Account account, Model model){
    // @CurrentUser Account account <-- 이 부분이 아래의 @AuthenticationPrincipa 어노테이션 적용을 받음
    // @AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
    //
    // 인증된 사용자가 anonymousUser 라면 account 에는 null 이 들어가고
    // account 가 null 이 아니면 로그인 됨
    //               ㄴ account 라는 property 를 추출해 옴
    // AccountService 클래스의 login(Account account) 메소드에서
    // 로그인할 때 사용한 Principal 에는
    if(account != null){
      model.addAttribute(account);
    }
    // templates 폴더의 index.html 로 이동
    return "index";

  }

  @GetMapping("/login")
  public String login(){
    // return "templates/login.html";
    return "/login";
  }


}
