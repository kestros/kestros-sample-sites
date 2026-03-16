package io.kestros.samples.content.dialogfieldsample;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import java.util.List;
import javax.annotation.Nullable;

/**
 * API interface for the Dialog Field Sample component.
 * Provides getter methods for each dialog field value.
 */
public interface KestrosDialogFieldSample extends KestrosBasicComponentElement {

  String RESOURCE_TYPE = "/libs/kestros/samples/components/content/dialog-field-sample";

  @Override
  default String getComponentResourceType() {
    return RESOURCE_TYPE;
  }

  @Nullable
  String getSampleText();

  @Nullable
  String getSampleTextarea();

  @Nullable
  String getSampleRichtext();

  @Nullable
  Boolean getSampleCheckbox();

  @Nullable
  String getSamplePath();

  @Nullable
  String getSampleSelect();

  @Nullable
  List<String> getSampleMultifield();

  @Nullable
  String getSampleTag();

  @Nullable
  String getSampleNumber();

  @Nullable
  String getSampleDate();

  @Nullable
  String getSampleDatetime();

  @Nullable
  Boolean getSampleToggle();

  @Nullable
  String getSampleRadio();

  @Nullable
  String getSampleHidden();

  @Nullable
  String getSampleImage();
}
