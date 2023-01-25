package com.global.settings;

import com.global.WithAccount;
import com.global.account.AccountRepository;
import com.global.account.AccountService;
import com.global.account.SignUpForm;
import com.global.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {
  @Autowired
  MockMvc mockMvc;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @BeforeEach
  void beforeEach(){
    /*
    WithAccountSecurityContextFactory 클래스의
    createSecurityContext() 메소드에서 진행
    SignUpForm signUpForm = new SignUpForm();
    signUpForm.setNickName("global");
    signUpForm.setEmail("global@gmail.com");
    signUpForm.setPassword("12345678");
    accountService.processNewAccount(signUpForm);
    */
  }

  @AfterEach
  void afterEach(){
    accountRepository.deleteAll();
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

    Account global = accountRepository.findByNickName("global");
    assertNull(global.getBio());
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
}