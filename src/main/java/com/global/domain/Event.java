package com.global.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of="id")
public class Event {

  @Id @GeneratedValue
  private Long id;

  @ManyToOne
  // @JoinColumn(name = "study_id")
  private Study study;

  @ManyToOne
  private Account account;

  @ManyToOne
  private Account createdBy;

  // 모임 제목
  private String title;

  // 모임 자세한 소개글
  @Lob
  private String description;

  // 모임 개설한 시각
  private LocalDateTime createDateTime;

  // 모임 등록 마감 시각 : 접수 마감
  private LocalDateTime endEnrollmentDateTime;

  // 모임 시작 시각
  private LocalDateTime startDateTime;

  // 모임 종료 시각
  @Column(nullable = false)
  private LocalDateTime endDateTime;

  // 참가 신청 제한 개수 : 등록 제한
  private int limitOfEnrollment;

  // Enrollment 를 저장하는 List
  @OneToMany(mappedBy = "event")
  private List<Enrollment> enrollmentList = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  private EventType eventType;


}
