package com.global.event;

import com.global.domain.Account;
import com.global.domain.Event;
import com.global.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;
  public Event createEvent(Event event, Study study, Account account) {
    // 모임을 개설하는 사람
    event.setCreatedBy(account);
    // 모임 개설 일시
    event.setCreatedDateTime(LocalDateTime.now());
    // 모임이 속한 Study
    event.setStudy(study);


    return eventRepository.save(event);
  }
}
