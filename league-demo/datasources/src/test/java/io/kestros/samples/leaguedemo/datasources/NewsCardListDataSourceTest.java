package io.kestros.samples.leaguedemo.datasources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;

public class NewsCardListDataSourceTest {

  private NewsCardListDataSource datasource;
  private Resource resource;
  private ValueMap valueMap;

  @Before
  public void setUp() {
    datasource = spy(new NewsCardListDataSource());
    resource = mock(Resource.class);
    valueMap = mock(ValueMap.class);
    when(resource.getValueMap()).thenReturn(valueMap);
    doReturn(resource).when(datasource).getResource();
  }

  @Test
  public void getMaxArticles_returnsDefault_whenNoProperty() {
    when(valueMap.get("maxRows", 4)).thenReturn(4);
    assertEquals(4, datasource.getMaxArticles());
  }

  @Test
  public void getMaxArticles_returnsOverride_whenPropertySet() {
    when(valueMap.get("maxRows", 4)).thenReturn(3);
    assertEquals(3, datasource.getMaxArticles());
  }
}
