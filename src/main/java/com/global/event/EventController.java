package com.global.event;

import com.global.account.CurrentUser;
import com.global.domain.Account;
import com.global.domain.Event;
import com.global.domain.Study;
import com.global.event.form.EventForm;
import com.global.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

  private final StudyService studyService;
  private final EventService eventService;

  // ModelMapper
  private final ModelMapper modelMapper;

  @GetMapping("/new-event")
  public String newEventForm(@CurrentUser Account account,
                             @PathVariable String path, Model model){
    Study study = studyService.getStudyToUpdateStatus(account, path);

    model.addAttribute(study);
    model.addAttribute(account);
    model.addAttribute(new EventForm());

    return "event/form";
  }

  // 모임 개설하기 버튼(submit)
  @PostMapping("/new-event")
  public String newEventSubmit(@CurrentUser Account account, @PathVariable String path,
                               @Valid EventForm eventForm, Errors errors, Model model){
    Study study = studyService.getStudyToUpdateStatus(account, path);

    if(errors.hasErrors()){
      model.addAttribute(account);
      model.addAttribute(study);
      return "event/form";
    }

    eventService.createEvent(modelMapper.map(eventForm, Event.class), study, account);
  }
}
