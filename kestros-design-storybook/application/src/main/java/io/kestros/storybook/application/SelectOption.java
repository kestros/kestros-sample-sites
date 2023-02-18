package io.kestros.storybook.application;

public class SelectOption {

  private String displayValue;
  private String value;

  public SelectOption(String displayValue, String value) {
    this.displayValue = displayValue;
    this.value = value;
  }

  public String getText() {
    return displayValue;
  }

  public String getValue() {
    return value;
  }
}
