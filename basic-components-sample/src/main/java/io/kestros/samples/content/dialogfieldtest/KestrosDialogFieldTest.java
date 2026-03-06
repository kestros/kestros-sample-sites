package io.kestros.samples.content.dialogfieldtest;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import javax.annotation.Nullable;

/**
 * API interface for the Dialog Field Test sample component.
 * Provides getter methods for each dialog field value.
 */
public interface KestrosDialogFieldTest extends KestrosBasicComponentElement {

  String RESOURCE_TYPE = "/libs/kestros/samples/components/content/dialog-field-test";

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
