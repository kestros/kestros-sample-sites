package io.kestros.samples.content.dialogfieldtest;

import io.kestros.cms.components.basic.core.BaseDataSourceComponent;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

/**
 * Rendering context class for the Dialog Field Test component.
 * Delegates to the datasource for each field value.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class DialogFieldTestDataSourceComponent
    extends BaseDataSourceComponent<KestrosDialogFieldTest>
    implements KestrosDialogFieldTest {

  @Override
  @Nullable
  public String getSampleText() {
    return getComponentData().getSampleText();
  }

  @Override
  @Nullable
  public String getSampleTextarea() {
    return getComponentData().getSampleTextarea();
  }

  @Override
  @Nullable
  public String getSampleRichtext() {
    return getComponentData().getSampleRichtext();
  }

  @Override
  @Nullable
  public Boolean getSampleCheckbox() {
    return getComponentData().getSampleCheckbox();
  }

  @Override
  @Nullable
  public String getSamplePath() {
    return getComponentData().getSamplePath();
  }

  @Override
  @Nullable
  public String getSampleSelect() {
    return getComponentData().getSampleSelect();
  }

  @Override
  @Nullable
  public String getSampleTag() {
    return getComponentData().getSampleTag();
  }

  @Override
  @Nullable
  public String getSampleNumber() {
    return getComponentData().getSampleNumber();
  }

  @Override
  @Nullable
  public String getSampleDate() {
    return getComponentData().getSampleDate();
  }

  @Override
  @Nullable
  public String getSampleDatetime() {
    return getComponentData().getSampleDatetime();
  }

  @Override
  @Nullable
  public Boolean getSampleToggle() {
    return getComponentData().getSampleToggle();
  }

  @Override
  @Nullable
  public String getSampleRadio() {
    return getComponentData().getSampleRadio();
  }

  @Override
  @Nullable
  public String getSampleHidden() {
    return getComponentData().getSampleHidden();
  }

  @Override
  @Nullable
  public String getSampleImage() {
    return getComponentData().getSampleImage();
  }
}
