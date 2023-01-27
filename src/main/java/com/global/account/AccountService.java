package com.global.account;

import com.global.domain.Account;
import com.global.domain.Tag;
import com.global.domain.Zone;
import com.global.settings.form.Notifications;
import com.global.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// @Transactional : AccountService 클래스의 모든 메소드 작업이
//                  Transaction 안에서 진행되도록 설정함
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
  private final AccountRepository accountRepository;
  private final JavaMailSender javaMailSender;
  private final PasswordEncoder passwordEncoder;
  // private final AuthenticationManager authenticationManager;

  private final ModelMapper modelMapper;

  public Account processNewAccount(SignUpForm signUpForm) {
    Account newAccount = saveNewAccount(signUpForm);
    // 이메일 보내기 전에 토큰값 생성하기
    newAccount.generateEmailCheckToken();
    sendSignUpConfirmEmail(newAccount);
    return newAccount;
  }

  private Account saveNewAccount(@Valid SignUpForm signUpForm) {
    Account account = Account.builder()
      .email(signUpForm.getEmail())
      .nickName(signUpForm.getNickName())
      .password(passwordEncoder.encode(signUpForm.getPassword()))
      .studyCreatedByWeb(true)
      .studyEnrollmentResultByWeb(true)
      .studyUpdatedByWeb(true)
      .build();

    Account newAccount = accountRepository.save(account);
    return newAccount;
  }

  public void sendSignUpConfirmEmail(Account newAccount) {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    // 토큰값에 해당하는 이메일 주소 받기
    simpleMailMessage.setTo(newAccount.getEmail());
    // 이메일 제목
    simpleMailMessage.setSubject("회원 가입 인증");
    // 이메일 본문
    // simpleMailMessage.setText("/check-email-token?token=이메일보내기전에생성한토큰값&email=토큰값에해당하는이메일주소");
    simpleMailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
      + "&email=" + newAccount.getEmail());
    javaMailSender.send(simpleMailMessage);
  }

  // password 를 encoding 하기 때문에 아래의 방법으로 로그인함
  public void login(Account account) {
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                                                        // account.getNickName(),
                                                        new UserAccount(account),
                                                        account.getPassword(),
                                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                                      );
    // Spring 에서 제공하는 SecurityContextHolder 에서
    // context 를 받아와서 인증하기
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(token);
  }

  // readOnly = true : data 를 변경하는 것이 아니고, 로그인 할 때 확인만 함
  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String emailOrNickName) throws UsernameNotFoundException {
    // findByEmail() 로 email 로 로그인하는지 알아보기
    Account account = accountRepository.findByEmail(emailOrNickName);
    // 이메일로 로그인하는 게 아니면
    // findByNickName() 로 nickName 로 로그인하는지 알아보기
    if(account == null){
      account = accountRepository.findByNickName(emailOrNickName);
    }

    // nickName 으로도 로그인하는 것이 아니라면
    // UsernameNotFoundException 예외를 발생시킴
    //  ㄴ email 또는 password 가 잘못되었다고 return 함
    if(account == null){
      throw new UsernameNotFoundException(emailOrNickName);
    }

    // 현재 입력한 email 이나 nickName 에 해당하는 user 가 있는 경우
    //  ㄴ Principal 에 해당하는 객체를 넘김
    //       ㄴ Spring Security 가 제공하는 User 상속하는 UserAccount 객체
    return new UserAccount(account);
  }

  // - Entity 객체 변경은 반드시 Transaction 안에서 해야 함
  //     ㄴ Transaction 종료 직전이나 필요한 시점에 변경 사항을 DB 에 반영할 수 있기 때문
  // 데이터를 변경하는 작업을 함
  public void completeSignUp(Account account) {
    account.completeSignUp();
    login(account);
  }

  public void updateProfile(Account account, Profile profile) {
    // account 의 정보 변경
    //
    //
    //
    //
    modelMapper.map(profile, account);
    // account.setBio(profile.getBio());
    // account.setUrl(profile.getUrl());
    // account.setOccupation(profile.getOccupation());
    // account.setLocation(profile.getLocation());

    // 프로필 사진 업데이트 처리 : 이미지를 가져와서 넣어줌
    account.setProfileImage(profile.getProfileImage());

    // account 객체의 멤버변수 값이 변경된 것을 DB 에도 반영
    accountRepository.save(account);
  }

  // SettingsController 의 public updatePassword() 메소드에서 호출
  public void updatePassword(Account account, String newPassword) {
    // account 의 비밀번호를 새로운 비밀번호로 변경
    //
    //
    //
    account.setPassword(passwordEncoder.encode(newPassword));
    // 명시적으로 merge
    accountRepository.save(account);
  }

  public void updateNotifications(Account account, Notifications notifications) {
    modelMapper.map(notifications, account);
    /*
    account.setStudyUpdatedByEmail(notifications.isStudyCreatedByEmail());
    account.setStudyUpdatedByEmail(notifications.isStudyCreatedByWeb());
    account.setStudyUpdatedByEmail(notifications.isStudyEnrollmentResultByEmail());
    account.setStudyUpdatedByEmail(notifications.isStudyEnrollmentResultByWeb());
    account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
    account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByWeb());
     */
    accountRepository.save(account);
  }

  public void updateNickName(Account account, String nickName){
    account.setNickName(nickName);
    accountRepository.save(account);
    login(account);
  }

  public void sendLoginLink(Account account) {
    account.generateEmailCheckToken();
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(account.getEmail());
    simpleMailMessage.setSubject("Global Study Cafe 로그인 링크입니다");
    simpleMailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                              "&email=" + account.getEmail());
    javaMailSender.send(simpleMailMessage);
  }

  public void addTag(Account account, Tag tag) {
    // Account 는 persistence 객체가 아니라서 (
    // Account 를 먼저 읽어야 함
    Optional<Account> byId = accountRepository.findById(account.getId());
    // a = account 객체
    byId.ifPresent(a -> a.getTags().add(tag));
  }

  public Set<Tag> getTags(Account account) {
    Optional<Account> byId = accountRepository.findById(account.getId());

    // 없으면 예외발생, 있으면 tag 정보 가져옴
    return byId.orElseThrow().getTags();
  }

  public void removeTag(Account account, Tag tag) {
    Optional<Account> byId = accountRepository.findById(account.getId());
    byId.ifPresent(a -> a.getTags().remove(tag));

  }

  public Set<Zone> getZones(Account account) {
    Optional<Account> byId = accountRepository.findById(account.getId());
    return byId.orElseThrow().getZones();
  }

  public void addZone(Account account, Zone zone) {
    Optional<Account> byId = accountRepository.findById(account.getId());
    byId.ifPresent(a -> a.getZones().add(zone));
  }

  public void removeZone(Account account, Zone zone) {
    Optional<Account> byId = accountRepository.findById(account.getId());
    byId.ifPresent(a -> a.getZones().remove(zone));
  }
}
