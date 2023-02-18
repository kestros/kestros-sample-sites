package io.kestros.storybook.design.typography.atoms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.storybook.design.StyleableElement;

public interface AnchorAtom extends StyleableElement {

  String getText();

  String getHref();

  @JsonIgnore
  default String getCssClass() {
    return "I'm a default, change me in the interface!";
  }

}
