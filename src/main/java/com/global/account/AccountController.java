package com.global.account;

import com.global.account.form.SignUpForm;
import com.global.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

  private final SignUpFormValidator signUpFormValidator;
  private final AccountService accountService;
  private final AccountRepository accountRepository;


  @InitBinder("signUpForm")
  public void initBinder(WebDataBinder webDataBinder){
    webDataBinder.addValidators(signUpFormValidator);

  }

  // localhost:8080/sign-up 을 주소표시줄에 입력했을 때
  // 자동으로 호출되는 메소드
  @GetMapping("/sign-up")
  public String signUpForm(Model model){
    model.addAttribute("signUpForm", new SignUpForm());
    return "account/sign-up";
  }

  // 회원가입 페이지에서 submit 버튼 눌렀을 때
  // 자동으로 호출되는 메소드
  @PostMapping("/sign-up")
  public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
    if(errors.hasErrors()){
      // 에러가 발생하면 다음 페이지로 넘어가지 않고
      // 다시 sign-up 페이지를 보여줌
      return "account/sign-up";
    }

    /*
    위에 작성한
    public void initBinder(WebDataBinder webDataBinder) 메소드에서
    검증 작업을 진행함

   ormValidator.validate(signUpForm, errors);
    if(errors.hasErrors()){
      // 에러가 발생하면 다음 페이지로 넘어가지 않고
      // 다시 sign-up 페이지를 보여줌
      return "account/sign-up";
    }
    */

    Account account = accountService.processNewAccount(signUpForm);
    accountService.login(account);
    /*
     이 부분은 Service 에서 실행하는 부분이라서
     Controller 에서 작성하지 않는 것이 좋음

    Account newAccount = accountService.saveNewAccount(signUpForm);
    // 이메일 보내기 전에 토큰값 생성하기
    newAccount.generateEmailCheckToken();
    accountService.sendSignUpConfirmEmail(newAccount);
   */
    // 회원가입 폼이 제대로 입력된 경우,
    // Home(/) 으로 이동함

    return "redirect:/";
  }

  // String token  <-- 가입하면서 받아온 token
  // 인증 메일 처리하는 부분
  @GetMapping("/check-email-token")
  public String checkEmailToken(String token, String email, Model model){
    // 가입자가 입력한 이메일이 정상적으로 등록되었는지 확인하기
    Account account = accountRepository.findByEmail(email);

    // 이동할 page
    String view = "account/check-email";

    // 가입자가 입력한 이메일이 정상적으로 등록 안 된 경우
    if (account == null){
      model.addAttribute("error", "wrong email");
      return view;
    }

    if (!account.isValidToken(token)){
      model.addAttribute("error", "wrong email");
      return view;
    }

    // 가입자가 입력한 이메일이 정상적으로 등록된 경우
    // token 확인하기
    if (!account.getEmailCheckToken().equals(token)){
      model.addAttribute("error", "wrong email");
      return view;
    }


    // 위의 검증을 거친 후 처리하는 부분
    //   - Entity 객체 변경은 반드시 Transaction 안에서 해야 함 :
    //             AccountService 클래스에서 completeSignUp() 메소드에 @Transactional 을 적용함
    //     ㄴ Transaction 종료 직전이나 필요한 시점에 변경 사항을 DB 에 반영할 수 있기 때문
    //  completeSignUp(account) : persistence 상태의 entity
    //  (Repository 를 구현한 객체들은 기본적으로 @Transactional 이 적용되어 있음)
    // completeSignUp(account) 에서의 account 객체는 persistence context 상태임
    //                                   ㄴ 88 행에서 생성된 account 객체
    accountService.completeSignUp(account);
    /*
      아래의 내용을 completeSignUp() 메소드로 옮김
      account.completeSignUp();
      accountService.login(account);
    */

    /*
    아래 code 를 Account 클래스의 completeSignUp() 메소드로 옮기기

    // 가입자가 입력한 이메일이 정상적으로 등록된 경우 + token 까지 확인된 경우
    account.setEmailVerified(true);
    // 가입한 시간 등록
    account.setJoinedAt(LocalDateTime.now());
    */
    // 몇 번째(accountRepository.count()) 가입자인지... 처리하기
    model.addAttribute("numberOfUser", accountRepository.count());
    // nickname
    model.addAttribute("nickName", account.getNickName());

    return view;
  }

  @GetMapping("/checkout-email")
  public String checkoutEmail(@CurrentUser Account account, Model model){
    model.addAttribute("email", account.getEmail());
    return "account/checkout-email";
  }

  // checkout-email.html 에서 인증 메일 다시 보내기 버튼 눌렀을 때
  // 주소표시줄에 localhost:8080/resend-confirm-email URL 이 입력되면
  // 자동으로 호출되는 메소드
  @GetMapping("/resend-confirm-email")
  public String resendConfirmEmail(@CurrentUser Account account, Model model){
    // 인증 메일을 1 시간 이내에 전송한 이력이 있다면 좀 기다렸다가 1 시간 지난 후 전송해야 함
    if(!account.canSendConfirmEmail()){
      model.addAttribute("error", "인증 이메일은 1 시간에 한 번만 전송 가능합니다.");
      model.addAttribute("email", account.getEmail());
      // 에러 메세지를 보여주고 같은 페이지를 다시 보여줌
      return "account/checkout-email";
    }
    // 인증 메일을 1 시간 이내에 전송한 이력이 없다면 전송하고 첫 페이지로 이동함
    accountService.sendSignUpConfirmEmail(account);
    return "redirect:/";
  }

  @GetMapping("/profile/{nickName}")
  public String viewProfile(@PathVariable String nickName,
                            @CurrentUser Account account, Model model){

    /*
      이 부분 AccountService getAccount() 메소드에 작성하고 호출

      Account byNickName = accountRepository.findByNickName(nickName);
    // nickName 이 들어오지 않은 경우
    if (nickName == null){
      throw  new IllegalArgumentException(nickName + " 에 해당하는 회원이 없습니다");
    }
    */
    Account accountToView = accountService.getAccount(nickName);


    // nickName 이 들어온 경우 <-- byNickName 을 메모리에 올려서
    //                              return 에서 지정한 html 페이지에서 사용할 수 있도록 함
    // model.addAttribute( ) 에 attributeValue(값) 만 지정하면,
    // attributeName(변수) 이름은 byNickName 에 들어가는 객체 type 의 camel case 로 사용함
    //                                                      ㄴ Account  <-- account
    model.addAttribute(accountToView);
    // model.addAttribute("account", byNickName);
    model.addAttribute("isCurrentUser", accountToView.equals(account));

    return "account/profile";
  }

  @GetMapping("/email-login")
  public String emailLoginForm(){
    return "account/email-login";
  }

  @PostMapping("/email-login")
  public String sendEmailLoginLink(String email, Model model,
                                   RedirectAttributes redirectAttributes){
    Account account = accountRepository.findByEmail(email);

    // 해당 이메일로 가입한 user 가 있는지 확인함
    if(account == null){
      model.addAttribute("error", "유효한 이메일 주소가 아닙니다");
      return "account/email-login";
    }
    // 이메일 로그인한지가 한 시간이 지났는지 안 지났는지 확인함
    // 악의적인 사용자가 이메일 로그인을 지속적으로 해서 시스템을 다운시키는 것을 방지하기 위함
    if(!account.canSendConfirmEmail()){
      model.addAttribute("error", "이메일 로그인은 한 시간에 한 번만 가능합니다.");
      // return "account/email-login";
    }
    // 유효한 이메일이면서 이메일 로그인한지가 한 시간이 지난 경우
    accountService.sendLoginLink(account);
    redirectAttributes.addFlashAttribute("message", "이메일 인증 메일을 발송했습니다.");
    return "redirect:/email-login";
  }

  // link 를 클릭하면 해당 이메일에 해당하는 account(계정)을 찾아서
  // account 가 null 이거나 유효한 token 이 아니면
  //  ㄴ 로그인할 수 없다는 메세지 보여줌
  // token 과 email 모두 유효한 경우에는 로그인함
  @GetMapping("/login-by-email")
  public String loginByEmail(String token, String email, Model model){
    Account account = accountRepository.findByEmail(email);
    String view = "account/logged-in-by-email";
    if(account == null || !account.isValidToken(token) ){
      model.addAttribute("error", "로그인할 수 없습니다.");
      return "account/logged-in-by-email";
    }
    accountService.login(account);

    // 이메일로 로그인했습니다. 비밀번호를 변경하세요. 라는  view 를 보여줌
    return "account/logged-in-by-email";
  }

}