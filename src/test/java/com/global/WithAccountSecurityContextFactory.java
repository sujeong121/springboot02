package com.global;

import com.global.account.AccountService;
import com.global.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;



@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

  private final AccountService accountService;

  @Override
  public SecurityContext createSecurityContext(WithAccount withAccount) {

    String nickName = withAccount.value();

    SignUpForm signUpForm = new SignUpForm();
    signUpForm.setNickName(nickName);
    signUpForm.setEmail(nickName + "gmail.com");
    signUpForm.setPassword("12345678");
    accountService.processNewAccount(signUpForm);

    //


    UserDetails principal = accountService.loadUserByUsername(nickName);



    Authentication authentication =
        new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());


    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);

    return securityContext;
  }
}
