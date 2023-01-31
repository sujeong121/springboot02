package com.global.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.WithAccount;
import com.global.account.AccountRepository;
import com.global.account.AccountService;
import com.global.domain.Account;
import com.global.domain.Zone;
import com.global.settings.form.ZoneForm;
import com.global.zone.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.global.settings.SettingsController.ROOT;
import static com.global.settings.SettingsController.SETTINGS;
import static com.global.settings.SettingsController.ZONES;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {
  @Autowired
  MockMvc mockMvc;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired AccountService accountService;

  @Autowired
  private ZoneRepository zoneRepository;

  private Zone testZone = Zone.builder()
                              .city("testCity")
                              .localNameOfCity("테스트도시")
                              .province("testProvince").build();
  /*
    @BeforeEach
    void beforeEach(){

      WithAccountSecurityContextFactory 클래스의
      createSecurityContext() 메소드에서 진행
      SignUpForm signUpForm = new SignUpForm();
      signUpForm.setNickName("global");
      signUpForm.setEmail("global@gmail.com");
      signUpForm.setPassword("12345678");
      accountService.processNewAccount(signUpForm);

    }
  */
  @BeforeEach
  void beforeEach(){
    zoneRepository.save(testZone);
  }

  @AfterEach
  void afterEach(){
    accountRepository.deleteAll();
    zoneRepository.deleteAll();
  }

  // @WithAccount("global") - 인증정보를 제공해 주는 annotation
  @WithAccount("global")
  @DisplayName("프로필 수정 폼 테스트")
  @Test
  void updateProfileForm() throws Exception{
    String bio = "자기소개를 수정하는 경우";
    mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("profile"));



  }


  // 요청을 보낼 때, 어떤 user 가 보내는지 설정
  //
  //
  //
  //
  //
  //
  //
  //
  // @WithUserDetails(value = "global", setupBefore = TestExecutionEvent.TEST_EXECUTION)
  @WithAccount("global")
  @DisplayName("프로필 수정 테스트 - 입력값이 정상인 경우")
  @Test
  void updateProfile() throws Exception{
    String bio = "소개글을 수정함";
    mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
            .param("bio","소개글을 수정함")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
            .andExpect(flash().attributeExists("message"));

    //
    Account global = accountRepository.findByNickName("global");
    //
    assertEquals(bio, global.getBio());
  }

  @WithAccount("global")
  @DisplayName("프로필 수정 테스트 - 입력값에 오류가 있는 경우")
  @Test
  void updateProfile_error() throws Exception{
    String bio = "자기소개를 35자가 넘게 길게 입력한 경우에는 오류가 발생하도록 Profile 클래스에 @Length(max=35) 라고 설정해 놓았음";
    mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                    .param("bio", bio)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW))
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("profile"))
            .andExpect(model().hasErrors());

    Account global = accountRepository.findByNickName("global");
    assertNull(global.getBio());
  }

  @WithAccount("global")
  @DisplayName("비밀번호 수정 테스트 - 입력값 정상")
  @Test
  void updatePassword_success() throws Exception{
   mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
           .param("newPassword", "12345678")
           .param("newPasswordConfirm", "12345678")
           .with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
           .andExpect(flash().attributeExists("message"));

   Account global = accountRepository.findByNickName("global");
   // passwordEncoder 주입 받아서 비밀번호 일치하는지 확인
   assertTrue(passwordEncoder.matches("12345678", global.getPassword()));
  }



  @WithAccount("global")
  @DisplayName("비밀번호 수정 테스트 - 비밀번호 일치하지 않는 경우")
  @Test
  void updatePassword_fail() throws Exception{
    mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
            .param("newPassword", "12345678")
            .param("newPasswordConfirm", "00000000")
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW))
            .andExpect(model().hasErrors())
            .andExpect(model().attributeExists("passwordForm"))
            .andExpect(model().attributeExists("account"));
  }



  @WithAccount("global")
  @DisplayName("닉네임 폼 수정하기")
  @Test
  void updateAccountForm() throws Exception{
    mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("nickNameForm"));
  }

  @WithAccount("global")
  @DisplayName("닉네임 수정하기 테스트 - 입력값 정상인 경우")
  @Test
  void updateAccount_success() throws Exception{
    String newNickName = "global3";
    mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                    .param("nickName", newNickName)
                    .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
            .andExpect(flash().attributeExists("message"));
    assertNotNull(accountRepository.findByNickName("global3"));
  }

  @WithAccount("global")
  @DisplayName("닉네임 수정하기 테스트 - 입력값 오류인 경우")
  @Test
  void updateAccount_fail() throws Exception{
    String newNickName = ")&^*&^%*^&*(*";
    mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
            .param("nickName", newNickName)
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW))
            .andExpect(model().hasErrors())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("nickNameForm"));

  }



  @WithAccount("global")
  @DisplayName("지역 정보 수정 폼 테스트")
  @Test
  void updateZonesForm() throws Exception{
    mockMvc.perform(get(ROOT + SETTINGS + ZONES))
            .andExpect(view().name(SETTINGS + ZONES))
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("allZones"))
            .andExpect(model().attributeExists("zones"));
  }


  @WithAccount("global")
  @DisplayName("지역 정보 추가 테스트 - add")
  @Test
  void addZone() throws Exception{
    // add 할 때, form 으로 입력받은 객체 생성함
    ZoneForm zoneForm = new ZoneForm();
    zoneForm.setZoneName(testZone.toString());

    mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/add")
            // add 할 때, content 로 JSON 을 넣어줌
            .contentType(MediaType.APPLICATION_JSON)
            .contentType(objectMapper.writeValueAsString(zoneForm))
            .with(csrf()))
            .andExpect(status().isOk());

    Account global = accountRepository.findByNickName("global");
    Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
    assertTrue(global.getZones().contains(zone));
  }

  @WithAccount("global")
  @DisplayName("지역 정보 삭제 테스트 - remove")
  @Test
  void removeZone() throws Exception{
    Account global = accountRepository.findByNickName("global");
    Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
    accountService.addZone(global, zone);

    // remove 할 때, form 으로 입력받은 객체 생성함
    ZoneForm zoneForm = new ZoneForm();
    zoneForm.setZoneName(testZone.toString());

    mockMvc.perform(post(ROOT + SETTINGS + ZONES + "/remove")
            // add 할 때, content 로 JSON 을 넣어줌
            .contentType(MediaType.APPLICATION_JSON)
            .contentType(objectMapper.writeValueAsString(zoneForm))
            .with(csrf()))
            .andExpect(status().isOk());

    assertFalse(global.getZones().contains(zone));
  }
}