package io.kestros.storybook.design.typography.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface FontSize {
  String getValue();

  @JsonIgnore
  String getDisplayText();
}
