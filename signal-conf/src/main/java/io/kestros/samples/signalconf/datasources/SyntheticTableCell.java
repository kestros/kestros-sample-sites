package io.kestros.samples.signalconf.datasources;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.core.BaseContainerSyntheticResource;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Synthetic Kestros table cell used by signal-conf datasources to render schedule cells.
 */
@SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
public class SyntheticTableCell extends BaseContainerSyntheticResource implements KestrosTableCell {

  private final String text;

  /**
   * Constructs a synthetic table cell.
   *
   * @param text cell text.
   * @param dataSource owning datasource.
   * @param resourcePrefix resource name prefix used for synthetic naming.
   * @param forcedResourceName explicit synthetic resource name, may be {@code null}.
   * @throws ComponentConfigurationException if the synthetic resource cannot be constructed.
   */
  public SyntheticTableCell(@Nonnull String text,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.text = text;
  }

  /**
   * Returns the cell's text content.
   *
   * @return cell text, or {@code null} if none was provided.
   */
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
