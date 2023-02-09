package com.global.event.form;

import com.global.domain.EventType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
// DTO 역할 (Data transfer Object)
public class EventForm {

  // 제목
  @NotBlank
  @Length(max=50)
  private String title;

  // 설명글
  private String description;

  // 모집 방법 : 기본값을 선착순으로 함
  private EventType eventType = EventType.FCFS;

  // 접수 마감 날짜 "2000-10-31T01:30:00.000-05:00"
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime endEnrollmentDateTime;

  // 모임 시작 날짜
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime startDateTime;

  // 모임 종료 날짜
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime endDateTime;

  // 모집 인원 : 최소 인원 2 명
  @Min(2)
  private Integer limitOfEnrollments = 2;

}
