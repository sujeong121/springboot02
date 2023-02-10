package com.global.event;

import com.global.account.CurrentUser;
import com.global.domain.Account;
import com.global.domain.Event;
import com.global.domain.Study;
import com.global.event.form.EventForm;
import com.global.event.validator.EventValidator;
import com.global.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

  private final StudyService studyService;
  private final EventService eventService;

  // ModelMapper
  private final ModelMapper modelMapper;

  private final EventValidator eventValidator;
  private final EventRepository eventRepository;

  @InitBinder("eventForm")
  public void initBinder(WebDataBinder webDataBinder){
    webDataBinder.addValidators(eventValidator);
  }

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

    Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), study,  account);
    return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
  }

  @GetMapping("/event/{id}")
  public String getEvent(@CurrentUser Account account, @PathVariable String path,
                         @PathVariable Long id, Model model){
    model.addAttribute(account);
    model.addAttribute(eventRepository.findById(id).orElseThrow());
    model.addAttribute(studyService.getStudy(path));
    return "event/view";
  }
}
