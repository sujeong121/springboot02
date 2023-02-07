package com.global.study.form;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class StudyDescriptionForm {

  @NotBlank
  @Length(max = 100)
  private String shortDescription;

  @NotBlank
  private String fullDescription;

}
