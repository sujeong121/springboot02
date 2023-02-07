package com.global.tag;

import com.global.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
  private final TagRepository tagRepository;

  public Tag findOrCreateNew(String tagTitle) {
    Tag tag = tagRepository.findByTitle(tagTitle);
    // 매개변수로 들어온 tagTitle 에 해당하는
    // Tag 객체가 아직 DB 에 없다면 저장
    //  ㄴ 중복되지 않게 저장
    if(tag == null){
      tag = tagRepository.save(Tag.builder().title(tagTitle).build());
    }
    return tag;
  }
}
