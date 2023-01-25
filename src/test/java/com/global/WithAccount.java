package com.global;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountSecurityContextFactory.class)
public @interface WithAccount {
  // 필요한 account 의 정보 받기 (여기서는 nickName 정보만)
  // value() 메소드로 nickName 을 받아옴
  String value();
}
