package com.global.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.account.AccountService;
import com.global.account.CurrentUser;
import com.global.domain.Account;
import com.global.domain.Tag;
import com.global.domain.Zone;
import com.global.settings.form.*;
import com.global.settings.validator.NickNameValidator;
import com.global.settings.validator.PasswordFormValidator;
import com.global.tag.TagRepository;
import com.global.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


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

  static final String ROOT = "/";
  static final String SETTINGS = "settings";
  static final String ZONES = "/zones";

  // settings/profile 문자열을 static 변수에 저장
  static final String SETTINGS_PROFILE_VIEW = "settings/profile";
  // static final String SETTINGS_PROFILE_URL = "/settings/profile";
  static final String SETTINGS_PROFILE_URL = "/" + SETTINGS_PROFILE_VIEW;

  static final String SETTINGS_PASSWORD_VIEW = "settings/password";
  // static final String SETTINGS_PASSWORD_URL = "/settings/password";
  static final String SETTINGS_PASSWORD_URL = "/" + SETTINGS_PASSWORD_VIEW;

  static final String SETTINGS_NOTIFICATIONS_VIEW = "settings/notifications";
  static final String SETTINGS_NOTIFICATIONS_URL = "/" + SETTINGS_NOTIFICATIONS_VIEW;

  static final String SETTINGS_ACCOUNT_VIEW = "settings/account";
  static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW;

  static final String SETTINGS_TAGS_VIEW = "settings/tags";
  static final String SETTINGS_TAGS_URL = "/" + SETTINGS_TAGS_VIEW;

  static final String SETTINGS_ZONES_VIEW = "settings/zones";
  static final String SETTINGS_ZONES_URL = "/" + SETTINGS_ZONES_VIEW;



  // Service type 의 멤버변수 선언
  private final AccountService accountService;

  // SettingsController 에서 ModelMapper 설정하기
  // @RequiredArgsConstructor 에 의해서 Spring 으로부터 주입 받기
  private final ModelMapper modelMapper;

  private final NickNameValidator nickNameValidator;

  private final TagRepository tagRepository;
  private final ZoneRepository zoneRepository;

  // Java 객체를 Json 으로 변환함
  private final ObjectMapper objectMapper;

  // PasswordFormValidator 를 Bean 으로 등록하지 않고
  // initBinder 를 사용해서 객체를 생성
  @InitBinder("passwordForm")
  public void initBinder(WebDataBinder webDataBinder){
    webDataBinder.addValidators(new PasswordFormValidator());
  }

  @InitBinder("nickNameForm")
  public void nickNameInitBinder(WebDataBinder webDataBinder){
    webDataBinder.addValidators(nickNameValidator);
  }

  // 주소표시줄에 /settings/profile 요청이 들어오면
  // 자동으로 호출되는 메소드
  // @CurrentUser <-- 현재 user(현재 로그인 상태인 회원)
  //      정보를 가져오기 위한 Annotation
  @GetMapping(SETTINGS_PROFILE_URL)
  public String updateProfileForm(@CurrentUser Account account, Model model){
    // model.addAttribute("account", account); 아래코드랑 같은 기능
    model.addAttribute(account);
    // model.addAttribute("profile", new Profile(account));
    // model.addAttribute(new Profile(account));
    //  ㄴ Profile 클래스에 매개변수 있는 생성자가 없어서 오류 발생
    //    ㄴ ModelMapper 로 처리함
    model.addAttribute(modelMapper.map(account, Profile.class));

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
    // model.addAttribute(new Notifications(account));
    model.addAttribute(modelMapper.map(account, Notifications.class));
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

  // tag 수정 뷰
  @GetMapping(SETTINGS_TAGS_URL)
  public String updateTags(@CurrentUser Account account, Model model){
    model.addAttribute(account);

    // form 입력 view 에서
    // 등록한 tag 정보들 조회
    // Account 가 가지고 있는 tag 정보 가져옴
    Set<Tag> tags = accountService.getTags(account);

    // view 에서 보여줄 때 Tag Entity type 이 아닌
    // 문자열로 전송
    // Tag type 의 list 가 아니고, 문자열 type 의 list
    model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

    // settings/tags
    return SETTINGS_TAGS_VIEW;
  }


  // 관심 주제 등록 기능 구현
  @PostMapping("/settings/tags/add")
  @ResponseBody
  public ResponseEntity addTag(@CurrentUser Account account,
                       @RequestBody TagForm tagForm){
    String title = tagForm.getTagTitle();

    // title 에 할당된 문자열과 같은 tag 가
    // 있는지 없는지 DB 에서 찾아봄
    /*
     Optional 을 사용하는 경우
    Tag tag = tagRepository.findByTitle(title).orElseGet(() -> tagRepository.save(Tag.builder()
                                                                                      .title(tagForm.getTagTitle())
                                                                                      .build()));
     */
    Tag tag = tagRepository.findByTitle(title);
    /* Optional 을 사용하지 않고 조건문으로 null 값을 처리하는 경우 */
    // tagRepository.findByTitle(title) 로 tag 를 가져오지 못하면 찾아서 할당
    if(title == null){
      tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
    }

    accountService.addTag(account, tag);

    return ResponseEntity.ok().build();
  }

  @PostMapping(SETTINGS_TAGS_URL + "/remove")
  @ResponseBody
  public ResponseEntity removeTag(@CurrentUser Account account,
                                  @RequestBody TagForm tagForm){
    String  title = tagForm.getTagTitle();
    Tag tag = tagRepository.findByTitle(title);
    if(tag == null){
      return ResponseEntity.badRequest().build();
    }
    accountService.removeTag(account, tag);
    return ResponseEntity.ok().build();
  }

  // nickName

  @GetMapping(SETTINGS_ACCOUNT_URL)
  public String updateAccountForm(@CurrentUser Account account, Model model){
    model.addAttribute(account);
    model.addAttribute(modelMapper.map(account, NickNameForm.class));
    return SETTINGS_ACCOUNT_VIEW;
  }

  @PostMapping(SETTINGS_ACCOUNT_URL)
  public String updateAccount(@CurrentUser Account account,
                              @Valid NickNameForm nickNameForm,
                              Errors errors, Model model,
                              RedirectAttributes redirectAttributes){
    if(errors.hasErrors()){
      model.addAttribute(account);
      return SETTINGS_ACCOUNT_VIEW;
    }

    accountService.updateNickName(account, nickNameForm.getNickName());
    redirectAttributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
    return "redirect:" + SETTINGS_ACCOUNT_URL;
  }


  @GetMapping(SETTINGS_ZONES_URL)
  public String updateZoneForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
    model.addAttribute(account);

    Set<Zone> zones = accountService.getZones(account);
    model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

    List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
    model.addAttribute("allZones", objectMapper.writeValueAsString(allZones));

    return SETTINGS_ZONES_VIEW;
  }

  // zone.html 의 ajax 에서






  @PostMapping(SETTINGS_ZONES_URL + "/add")
  @ResponseBody
  public ResponseEntity addZone(@CurrentUser Account account,
                                @RequestBody ZoneForm zoneForm){

    Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
    if(zone == null){
      return ResponseEntity.badRequest().build();
    }
    accountService.addZone(account, zone);
    return ResponseEntity.ok().build();
  }

  //
  @PostMapping(SETTINGS_ZONES_URL + "/remove")
  @ResponseBody
  public ResponseEntity removeZone(@CurrentUser Account account,
                                   @RequestBody ZoneForm zoneForm){

    Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
    if(zone == null){
      return ResponseEntity.badRequest().build();
    }
    accountService.removeZone(account, zone);
    return ResponseEntity.ok().build();
  }
}
