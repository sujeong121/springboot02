package com.global.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.global.account.CurrentUser;
import com.global.domain.Account;
import com.global.domain.Study;
import com.global.domain.Tag;
import com.global.domain.Zone;
import com.global.settings.form.TagForm;
import com.global.settings.form.ZoneForm;
import com.global.study.form.StudyDescriptionForm;
import com.global.tag.TagRepository;
import com.global.tag.TagService;
import com.global.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {

  private final StudyService studyService;
  private final ModelMapper modelMapper;
  private final TagRepository tagRepository;
  private final ObjectMapper objectMapper;
  private final TagService tagService;
  private final ZoneRepository zoneRepository;
  private final StudyRepository studyRepository;

  @GetMapping("/description")
  public String viewStudySetting(@CurrentUser Account account,
                                 @PathVariable String path, Model model) {
    Study study = studyService.getStudyToUpdate(account, path);
    model.addAttribute(account);
    model.addAttribute(study);
    model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
    return "study/settings/description";
  }

  @PostMapping("/description")
  public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path,
                                @Valid StudyDescriptionForm studyDescriptionForm, Errors errors,
                                Model model, RedirectAttributes redirectAttributes) {

    Study study = studyService.getStudyToUpdate(account, path);

    if(errors.hasErrors()){
      model.addAttribute(account);
      model.addAttribute(study);
      return "study/settings/description";
    }

    studyService.updateStudyDescription(study, studyDescriptionForm);
    redirectAttributes.addFlashAttribute("message", "study 정보 수정완료");
    return "redirect:/study/" + getPath(path) + "/settings/description";
  }

  private String getPath(String path){
    return URLEncoder.encode(path, StandardCharsets.UTF_8);
  }

  @GetMapping("/banner")
  public String studyImageForm(@CurrentUser Account account,
                               @PathVariable String path, Model model){
    Study study = studyService.getStudyToUpdate(account, path);
    model.addAttribute(account);
    model.addAttribute(study);
    return "study/settings/banner";
  }

  @PostMapping("/banner")
  public String studyImageSubmit(@CurrentUser Account account, @PathVariable String path,
                                 String image, RedirectAttributes redirectAttributes){
    Study study = studyService.getStudyToUpdate(account, path);
    studyService.updateStudyImage(study, image);
    redirectAttributes.addFlashAttribute("message", "배너 이미지 수정 완료");
    return "redirect:/study/" + getPath(path) + "/settings/banner";
  }

  @PostMapping("/banner/enable")
  public String enableStudyBanner(@CurrentUser Account account, @PathVariable String path){
    Study study = studyService.getStudyToUpdate(account, path);
    studyService.enableStudyBanner(study);
    return "redirect:/study/" + getPath(path) + "/settings/banner";
  }

  @PostMapping("/banner/disable")
  public String disableStudyBanner(@CurrentUser Account account, @PathVariable String path){
    Study study = studyService.getStudyToUpdate(account, path);
    studyService.disableStudyBanner(study);
    return "redirect:/study/" + getPath(path) + "/settings/banner";
  }

  @GetMapping("/tags")
  public String studyTagsForm(@CurrentUser Account account,
                              @PathVariable String path, Model model) throws JsonProcessingException {
    Study study = studyService.getStudyToUpdate(account, path);
    model.addAttribute(account);
    model.addAttribute(study);

    model.addAttribute("tags", study.getTags().stream()
                                                  .map(Tag::getTitle).collect(Collectors.toList()));
    List<String> allTagTitles = tagRepository.findAll().stream()
                                             .map(Tag::getTitle).collect(Collectors.toList());
    model.addAttribute("whiteList", objectMapper.writeValueAsString(allTagTitles));
    return "study/settings/tags";
  }

  @PostMapping("/tags/add")
  @ResponseBody
  public ResponseEntity addTage(@CurrentUser Account account, @PathVariable String path,
                                @RequestBody TagForm tagForm){
    Study study = studyService.getStudyToUpdate(account, path);
    Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
    studyService.addTag(study, tag);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/tags/remove")
  @ResponseBody
  public ResponseEntity removeTage(@CurrentUser Account account, @PathVariable String path,
                                   @RequestBody TagForm tagForm){
    Study study = studyService.getStudyToUpdate(account, path);
    Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
    if (tag == null) {
      return ResponseEntity.badRequest().build();
    }

    studyService.removeTag(study, tag);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/zones")
  public String studyZonesForm(@CurrentUser Account account,
                               @PathVariable String path, Model model) throws JsonProcessingException {
    Study study = studyService.getStudyToUpdate(account, path);
    model.addAttribute(account);
    model.addAttribute(study);
    model.addAttribute("zones", study.getZones().stream()
                                                    .map(Zone::toString).collect(Collectors.toList()));
    List<String> allZones = zoneRepository.findAll().stream()
                                                    .map(Zone::toString).collect(Collectors.toList());
    model.addAttribute("whiteList", objectMapper.writeValueAsString(allZones));
    return "study/settings/zones";
  }

  @PostMapping("/zones/add")
  @ResponseBody
  public ResponseEntity addZones(@CurrentUser Account account, @PathVariable String path,
                                 @RequestBody ZoneForm zoneForm){
    Study study = studyService.getStudyToUpdate(account, path);
    Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
    if(zone == null){
      return ResponseEntity.badRequest().build();
    }
    studyService.addZone(study, zone);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/zones/remove")
  @ResponseBody
  public ResponseEntity removeZone(@CurrentUser Account account, @PathVariable String path,
                                   @RequestBody ZoneForm zoneForm){
    Study study = studyService.getStudyToUpdate(account, path);
    Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
    if(zone == null){
      return ResponseEntity.badRequest().build();
    }
    studyService.removeZone(study, zone);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/study")
  public String studySettingForm(@CurrentUser Account account, @PathVariable String path, Model model){
    Study study = studyService.getStudyToUpdate(account, path);
    model.addAttribute(account);
    model.addAttribute(study);
    return "study/settings/study";
  }

  @PostMapping("study/publish")
  public String publishStudy(@CurrentUser Account account, @PathVariable String path,
                             RedirectAttributes redirectAttributes){
    Study study = studyService.getStudyToUpdateStatus(account, path);
    studyService.publish(study);
    redirectAttributes.addFlashAttribute("message", "Study 를 공개함");
    return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
  }

  @PostMapping("/study/close")
  public String closeStudy(@CurrentUser Account account, @PathVariable String path,
                           RedirectAttributes redirectAttributes){
    Study study = studyService.getStudyToUpdateStatus(account, path);
    studyService.close(study);
    redirectAttributes.addFlashAttribute("message", "Study 를 종료함");
    return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
  }

  @PostMapping("/recruit/start")
  public String startRecruit(@CurrentUser Account account, @PathVariable String path,
                             Model model, RedirectAttributes redirectAttributes){
    Study study = studyService.getStudyToUpdateStatus(account, path);

    if(!study.canUpdateRecruiting()){
      redirectAttributes.addFlashAttribute("message", "1 시간에 한 번만 멤버 모집 설정을 변경할 수 있습니다.");
      return "redirect:/study" + study.getEncodedPath() + "/settings/study";
    }

    studyService.startRecruit(study);
    redirectAttributes.addFlashAttribute("message", "멤버 모집을 시작합니다.");
    return "redirect:/study" + study.getEncodedPath() + "/settings/study";
  }

  @PostMapping("/recruit/stop")
  public String stopRecruit(@CurrentUser Account account, @PathVariable String path,
                            Model model, RedirectAttributes redirectAttributes){
    Study study = studyService.getStudyToUpdateStatus(account, path);

    if(!study.canUpdateRecruiting()){
      redirectAttributes.addFlashAttribute("message", "1 시간에 한 번만 멤버 모집 설정을 변경할 수 있습니다.");
      return "redirect:/study" + study.getEncodedPath() + "/settings/study";
    }

    studyService.stopRecruit(study);
    redirectAttributes.addFlashAttribute("message", "멤버 모집을 종료합니다.");
    return "redirect:/study" + study.getEncodedPath() + "/settings/study";
  }

  @PostMapping("/study/path")
  public String updateStudyPath(@CurrentUser Account account, @PathVariable String path,
                                @RequestParam String newPath,
                                Model model, RedirectAttributes redirectAttributes){
    Study study = studyService.getStudyToUpdateStatus(account, path);

    if(!studyService.isValidPath(newPath)){
      model.addAttribute(account);
      model.addAttribute(study);
      model.addAttribute("studyPathError", "입력하신 path 는 사용할 수 없습니다. 다른 path 를 입력해 주세요.");
      return "study/settings/study";
    }

    studyService.updateStudyPath(study, newPath);
    redirectAttributes.addFlashAttribute("message", "Study 경로를 수정했습니다.");
    return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
  }

  @PostMapping("/study/title")
  public String updateStudyTitle(@CurrentUser Account account, @PathVariable String path,
                                 @RequestParam String newTitle,
                                 Model model, RedirectAttributes redirectAttributes){
    Study study = studyService.getStudyToUpdateStatus(account, path);

    if(!studyService.isValidTitle(newTitle)){
      model.addAttribute(account);
      model.addAttribute(study);
      model.addAttribute("studyTitleError", "입력하신 이름은 사용할 수 없습니다. 다른 이름을 입력해 주세요.");
      return "study/settings/study";
    }

    studyService.updateStudyTitle(study, newTitle);
    redirectAttributes.addFlashAttribute("message", "Study 이름을 수정했습니다.");
    return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
  }

  @PostMapping("/study/remove")
  public String removeStudy(@CurrentUser Account account, @PathVariable String path){
    Study study = studyService.getStudyToUpdate(account, path);
    studyService.remove(study);
    return "redirect:/";
  }


}
