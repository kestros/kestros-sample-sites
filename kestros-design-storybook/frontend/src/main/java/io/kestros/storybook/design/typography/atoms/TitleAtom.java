package io.kestros.storybook.design.typography.atoms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.storybook.design.StyleableElement;
import io.kestros.storybook.design.typography.configuration.FontSize;
import io.kestros.storybook.design.typography.configuration.HeadingLevel;

public interface TitleAtom extends StyleableElement {

  @JsonIgnore
  default String getCssClass() {
    return "I'm a default, change me in the interface!";
  }

  String getText();

  FontSize getFontSize();

  HeadingLevel getHeadingLevel();

}
