package com.yeo_li.yeol_post.tag;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepository;
  
  public List<Tag> findOrCreateAll(List<String> tagNames) {
    List<Tag> result = new ArrayList<>();
    for (String name : tagNames) {
      Tag tag = tagRepository.findByTagName(name)
          .orElseGet(() -> tagRepository.save(new Tag(name)));
      result.add(tag);
    }

    return result;
  }

}
