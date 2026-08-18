package io.kestros.samples.acpl.application.datasources;

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
  private final List<KestrosBasicComponentElement> content;

  public SyntheticTableCell(@Nonnull String text,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    this(text, new ArrayList<>(), dataSource, resourcePrefix, forcedResourceName);
  }

  /**
   * Cell whose visible content is a set of nested component elements (e.g. a linked crest image plus
   * an abbreviation link) rendered through the cell's content area, instead of plain text.
   */
  public SyntheticTableCell(@Nonnull String text,
      @Nonnull List<KestrosBasicComponentElement> content,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.text = text;
    this.content = content;
  }

  @Nullable
  public String getText() {
    return text;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getCellContentElements() {
    return new ArrayList<>(content);
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(content);
  }

  private boolean numeric;

  /** Numeric cells right-align in the table system. */
  public SyntheticTableCell asNumeric() {
    this.numeric = true;
    return this;
  }

  public boolean getNumeric() {
    return numeric;
  }
}