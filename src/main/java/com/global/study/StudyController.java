package com.global.study;

import com.global.account.CurrentUser;
import com.global.domain.Account;
import com.global.study.form.StudyForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudyController {
  // Model <-- 화면에 전달할 객체 저장 (Memory 에 저장함 name-value)
  @GetMapping("/new-study")
  public String newStudyForm(@CurrentUser Account account, Model model){
    model.addAttribute(account);
    // html 에서 받을 정보 메모리에 올림
    //
    model.addAttribute(new StudyForm());
    return "study/form";
  }
}
