package com.global.domain;

import com.global.account.UserAccount;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// withAllRelations -> withAll 줄일 수 있음
@NamedEntityGraph(name="Study.withAllRelations", attributeNodes = {
  @NamedAttributeNode("tags"),
  @NamedAttributeNode("zones"),
  @NamedAttributeNode("managers"),
  @NamedAttributeNode("members")})
@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {

  @Id @GeneratedValue
  private Long id;

  // 해당 Study 를 개설하는 사람을 기본적으로 매니저가 되며
  // 추후에 일반 멤버들에게 매니저 권한을 부여할 수 있도록 설정
  // 매니저 권한 위임 가능하게 설정
  @ManyToMany
  private Set<Account> managers = new HashSet<>();

  @ManyToMany
  private Set<Account> members = new HashSet<>();

  @Column(unique = true)
  private String path;

  private String title;

  private String shortDescription;

  // @Basic(fetch=FetchType.EAGER)
  //  ㄴ Study 정보 조회할 때, Lob type data 도 가져오도록 설정
  /**/
  @Lob @Basic(fetch=FetchType.EAGER)
  private String fullDescription;

  @Lob @Basic(fetch=FetchType.EAGER)
  private String image;

  @ManyToMany
  private Set<Tag> tags;

  @ManyToMany
  private Set<Zone> zones;

  // Study 공개 시간
  private LocalDateTime publishedDateTime;

  // Study 종료 시간
  private LocalDateTime closedDateTime;

  // 인원 모집 개시와 중단 시간 설정
  // 모집 상태와 중단 상태를 너무 자주 변경하지 못하도록
  // 시간 제한
  private LocalDateTime recruitingUpdateDateTime;

  // 현재 모집 중인지 아닌지 여부
  private  boolean recruiting;

  // Study 공개 여부
  private boolean published;

  // Study 종료 여부
  private boolean closed;

  // Banner 사용 여부
  private boolean useBanner;

  public void addManager(Account account) {
    this.managers.add(account);
  }

  public boolean isJoinable(UserAccount userAccount){
    Account account = userAccount.getAccount();
    return this.isPublished() && this.isRecruiting()
      && !this.members.contains(account) && !this.managers.contains(account);
  }

  public boolean isMember(UserAccount userAccount){
    return this.members.contains(userAccount.getAccount());
  }
  public boolean isManager(UserAccount userAccount){
    return this.managers.contains(userAccount.getAccount());
  }
}
