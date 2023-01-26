package com.global.settings.form;

import com.global.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

/*
  com.global.domain.Account 클래스에 있는
  bio, url, occupation, location.
  이 네 개의 정보를 받아오는 클래스

  @NoArgsConstructor 기본 생성자 자동으로 만들어줌
*/
@Data
// @NoArgsConstructor
public class Profile {

  private String bio;
  private String url;
  private String occupation;
  private String location;

  private String profileImage;
/*
  public Profile(Account account){
    // ModelMapper modelMapper = new ModelMapper();
    // modelMapper.map(this, account);
    this.bio = account.getBio();
    this.url = account.getUrl();
    this.occupation = account.getOccupation();
    this.location = account.getLocation();
    this.profileImage = account.getProfileImage();
  }
 */
}
