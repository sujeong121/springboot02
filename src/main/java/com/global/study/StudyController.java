package com.global.study;

import com.global.account.CurrentUser;
import com.global.domain.Account;
import com.global.domain.Study;
import com.global.study.form.StudyForm;
import com.global.study.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

  private final StudyService studyService;

  // StudyForm 에서 Study 로 data 옮기기 위해
  private final ModelMapper modelMapper;

  private final StudyRepository studyRepository;
  private final StudyFormValidator studyFormValidator;


  @InitBinder("studyForm")
  public void studyFormInitBinder(WebDataBinder webDataBinder){
    //
    // study 추가할 때, path 값 중복되는지 검증
    webDataBinder.addValidators(studyFormValidator);
  }


  // Model <-- 화면에 전달할 객체 저장 (Memory 에 저장함 name-value)
  @GetMapping("/new-study")
  public String newStudyForm(@CurrentUser Account account, Model model){
    model.addAttribute(account);
    // html 에서 받을 정보 메모리에 올림
    //
    model.addAttribute(new StudyForm());
    return "study/form";
  }

  @PostMapping("/new-study")
  public String newStudySubmit(@CurrentUser Account account,
                               @Valid StudyForm studyForm, Errors errors){
    if(errors.hasErrors()){
      return "study/form";
    }


    Study newStudy = studyService.createNewStudy(modelMapper.map(studyForm, Study.class), account);

    // url 에 한글 포함될 수 있으므로 URLEncoding 처리
    return "redirect:/study/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8);
  }

  @GetMapping("/study/{path}") // {path}: path 로 biding 받음
  public String viewStudy(@CurrentUser Account account,
                          @PathVariable String path, Model model){
    model.addAttribute(account);
    model.addAttribute(studyRepository.findByPath(path));
    return "study/view";
  }
}
