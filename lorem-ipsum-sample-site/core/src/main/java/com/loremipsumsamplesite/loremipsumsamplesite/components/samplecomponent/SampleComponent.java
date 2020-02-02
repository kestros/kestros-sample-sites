package com.loremipsumsamplesite.loremipsumsamplesite.components.samplecomponent;

import io.kestros.cms.foundation.content.BaseComponent;
import io.kestros.commons.structuredslingmodels.annotation.StructuredModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class,
       resourceType = "lorem-ipsum-sample-site/components/content/sample-component")
public class SampleComponent extends BaseComponent {

  public String getSampleProperty() {
    return getProperty("sampleProperty", StringUtils.EMPTY);
  }
}