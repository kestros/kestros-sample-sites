package io.kestros.storybook.design.foundationorganisms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kestros.storybook.design.StyleableElement;
import io.kestros.storybook.design.typography.atoms.AnchorAtom;
import io.kestros.storybook.design.typography.atoms.TitleAtom;
import java.util.StringJoiner;

public interface CardOrganism extends StyleableElement {

  @JsonIgnore
  default String getCssClass() {
    StringJoiner cssClass = new StringJoiner(" ");

    return cssClass.toString();
  }

  TitleAtom getTitle();

  AnchorAtom getCta();
}
