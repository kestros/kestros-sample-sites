package io.kestros.storybook.design.typography.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface HeadingLevel {

  String getValue();

  @JsonIgnore
  String getDisplayText();

  @JsonIgnore
  FontSize getDefaultFontSize();
}
