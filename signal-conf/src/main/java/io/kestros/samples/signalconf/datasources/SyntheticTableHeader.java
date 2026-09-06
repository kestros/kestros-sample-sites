package io.kestros.samples.signalconf.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.cms.components.basic.core.BaseSyntheticResource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Synthetic Kestros table header used by signal-conf datasources to render schedule headers.
 */
public class SyntheticTableHeader extends BaseSyntheticResource implements KestrosTableHeader {

  private final String text;

  /**
   * Constructs a synthetic table header.
   *
   * @param text header text.
   * @param dataSource owning datasource.
   * @param resourcePrefix resource name prefix used for synthetic naming.
   * @param forcedResourceName explicit synthetic resource name, may be {@code null}.
   * @throws ComponentConfigurationException if the synthetic resource cannot be constructed.
   */
  public SyntheticTableHeader(@Nonnull String text,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.text = text;
  }

  @Nullable
  @Override
  public String getText() {
    return text;
  }
}
