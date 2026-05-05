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

public class RecentResultsTableDataSourceTest {

  private RecentResultsTableDataSource datasource;
  private Resource resource;
  private ValueMap valueMap;

  @Before
  public void setUp() {
    datasource = spy(new RecentResultsTableDataSource());
    resource = mock(Resource.class);
    valueMap = mock(ValueMap.class);
    when(resource.getValueMap()).thenReturn(valueMap);
    doReturn(resource).when(datasource).getResource();
  }

  @Test
  public void getMaxResults_returnsDefault_whenNoProperty() {
    when(valueMap.get("maxRows", 5)).thenReturn(5);
    assertEquals(5, datasource.getMaxResults());
  }

  @Test
  public void getMaxResults_returnsOverride_whenPropertySet() {
    when(valueMap.get("maxRows", 5)).thenReturn(3);
    assertEquals(3, datasource.getMaxResults());
  }
}
