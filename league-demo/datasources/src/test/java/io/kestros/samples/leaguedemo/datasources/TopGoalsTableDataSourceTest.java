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

public class TopGoalsTableDataSourceTest {

  private TopGoalsTableDataSource datasource;
  private Resource resource;
  private ValueMap valueMap;

  @Before
  public void setUp() {
    datasource = spy(new TopGoalsTableDataSource());
    resource = mock(Resource.class);
    valueMap = mock(ValueMap.class);
    when(resource.getValueMap()).thenReturn(valueMap);
    doReturn(resource).when(datasource).getResource();
  }

  @Test
  public void getLimit_returnsDefault_whenNoProperty() {
    when(valueMap.get("maxRows", 5)).thenReturn(5);
    assertEquals(5, datasource.getLimit());
  }

  @Test
  public void getLimit_returnsOverride_whenPropertySet() {
    when(valueMap.get("maxRows", 5)).thenReturn(10);
    assertEquals(10, datasource.getLimit());
  }
}
