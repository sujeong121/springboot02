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
  public void createEvent(Event event, Study study, Account account) {
    event.setCreatedBy(account);
    event.setCreateDateTime(LocalDateTime.now());
    event.setStudy(study);


  }
}
