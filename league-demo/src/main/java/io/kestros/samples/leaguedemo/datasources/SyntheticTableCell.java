package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.core.BaseContainerSyntheticResource;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SyntheticTableCell extends BaseContainerSyntheticResource implements KestrosTableCell {

  private final String text;

  public SyntheticTableCell(@Nonnull String text,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.text = text;
  }

  @Nullable
  public String getText() {
    return text;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getCellContentElements() {
    return new ArrayList<>();
  }
}
