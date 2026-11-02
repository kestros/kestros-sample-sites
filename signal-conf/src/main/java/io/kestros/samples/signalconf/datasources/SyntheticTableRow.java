package io.kestros.samples.signalconf.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSyntheticResource;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Synthetic Kestros table row used by signal-conf datasources to render schedule rows.
 */
public class SyntheticTableRow extends BaseContainerSyntheticResource implements KestrosTableRow {

  private final List<KestrosTableCell> cells;

  /**
   * Constructs a synthetic table row.
   *
   * @param cells cells contained by this row.
   * @param dataSource owning datasource.
   * @param resourcePrefix resource name prefix used for synthetic naming.
   * @param forcedResourceName explicit synthetic resource name, may be {@code null}.
   * @throws ComponentConfigurationException if the synthetic resource cannot be constructed.
   */
  public SyntheticTableRow(@Nonnull List<KestrosTableCell> cells,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.cells = cells;
  }

  @Nonnull
  @Override
  public List<KestrosTableCell> getCellElements() {
    return new ArrayList<>(cells);
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(cells);
  }
}
