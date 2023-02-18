package io.kestros.storybook.application.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.mrbean.AbstractTypeMaterializer;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.storybook.application.services.ClassLookupService;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
public class JsonDataComponent extends BaseComponent {


  private static final Logger LOG = LoggerFactory.getLogger(JsonDataComponent.class);
  @OSGiService
  private ClassLookupService classLookupService;

  @SuppressFBWarnings()
  void instantiateFields(Object o, ClassLookupService classLookupService)
      throws IllegalAccessException {
    if (o == null || o.getClass() == null) {
      return;
    }
    Field[] fields = o.getClass().getDeclaredFields();
    List<Method> methods = new ArrayList<>();
    methods.addAll(Arrays.asList(o.getClass().getMethods()));
    methods.addAll(Arrays.asList(o.getClass().getDeclaredMethods()));


    for (Field field : fields) {
      boolean defaultMethod = false;
      for (Method method : methods) {
        String methodName = StringUtils.uncapitalize(method.getName().replaceFirst("get|is", ""));
        if (methodName.equals(field.getName())) {
          if (method.isDefault()) {
            defaultMethod = true;
          }
        }
      }
      field.setAccessible(true);
      if (defaultMethod) {
        LOG.debug("Default method found for field: " + field.getName());
        field.set(o, null);
        LOG.debug("{}", field.get(o));
      }
      if (!defaultMethod) {


        if (field.get(o) == null) {
          Type type = field.getType();
          Class<?> clazz = (Class<?>) type;
          Object instance = null;
          try {
            instance = clazz.newInstance();
          } catch (InstantiationException e) {
            instance = getDataObjetFromJson("{}", clazz.getCanonicalName(), classLookupService);
          }

          if (List.class.isAssignableFrom(clazz)) {
            try {
              instantiateList(clazz, field, instance, classLookupService);
            } catch (InstantiationException e) {
              LOG.error("Error instantiating list", e);
            }
          }

          field.set(o, instance);
          instantiateFields(instance, classLookupService);

        }
      }
    }
  }

  void instantiateList(Class<?> clazz, Field field, Object instance,
      ClassLookupService classLookupService)
      throws IllegalAccessException, InstantiationException {
    ParameterizedType listType = (ParameterizedType) field.getGenericType();
    Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];

    Object listTypeInstance = listClass.newInstance();

    instantiateFields(listTypeInstance, classLookupService);

    List<Object> list = (List<Object>) instance;
    list.add(listTypeInstance);
  }

  @SuppressFBWarnings("REC_CATCH_EXCEPTION")
  Object getDataObjetFromJson(String json, String elementInterfaceName,
      ClassLookupService classLookupService) {

    try {
      ObjectMapper mapper = new ObjectMapper();
      ClassLoader classLoader = classLookupService.getClassLoader(elementInterfaceName);
      TypeFactory typeFactory = TypeFactory.defaultInstance().withClassLoader(classLoader);
      mapper.setTypeFactory(typeFactory);
      AbstractTypeMaterializer materializer = new AbstractTypeMaterializer(classLoader);
      MrBeanModule mrBeanModule = new MrBeanModule(materializer);
      mapper.registerModule(mrBeanModule);
      Class clazz = classLookupService.getClass(elementInterfaceName);
      Object object = mapper.readValue(json, clazz);
      return object;
    } catch (Exception e) {
      LOG.error("Error instantiating data object.", e);
      return null;
    }
  }

  /**
   * Builds object for frontend from JSON data.
   *
   * @return Object for frontend from JSON data.
   */
  public Object getDataObject() {
    return getDataObjetFromJson(getJsonData(), getElementInterfaceName(), classLookupService);
  }

  /**
   * Builds an empty JSON object for an interface.
   *
   * @return Builds an empty JSON object for an interface.
   */
  public String getEmptyJsonConfiguration() {
    Object object = getDataObjetFromJson("{}", getElementInterfaceName(), classLookupService);
    //    try {
    try {
      instantiateFields(object, classLookupService);
    } catch (IllegalAccessException e) {
      LOG.info("Error instantiating fields", e);
      // todo log
    }
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    // ignore null properties in json
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      LOG.error("Error processing JSON", e);
      // todo
    }
    //      return mapper.writeValueAsString(Class.forName(getElementInterfaceName()).newInstance
    //      ());
    //    } catch (Exception e) {
    //      LOG.error("Error while creating empty json configuration", e);
    //    }
    return StringUtils.EMPTY;
  }

  /**
   * JSON Data.
   *
   * @return JSON Data.
   */
  public String getJsonData() {
    return getResource().getValueMap().get("jsonData", StringUtils.EMPTY);
  }

  /**
   * HTL Template to use.
   *
   * @return HTL Template to use.
   */
  public String getHtlTemplate() {
    return getResource().getValueMap().get("htlTemplate", StringUtils.EMPTY).split("--")[0];
  }

  public String getTemplateName() {
    return getResource().getValueMap().get("htlTemplate", StringUtils.EMPTY).split("--")[1];
  }

  /**
   * Returns the name of the interface that the data object implements.
   *
   * @return The name of the interface that the data object implements.
   */
  public String getElementInterfaceName() {
    return getResource().getValueMap().get("element", StringUtils.EMPTY);
  }

  public boolean isDebug() {
    return getResource().getValueMap().get("debug", false);
  }


  public String getDebugJsonConfiguration() {
    String json = getJsonData();
    if (StringUtils.isEmpty(json)) {
      json = "{}";
    }

    Object object = getDataObjetFromJson(json, getElementInterfaceName(), classLookupService);
    //    try {
    try {
      instantiateFields(object, classLookupService);
    } catch (IllegalAccessException e) {
      LOG.info("Error instantiating fields", e);
      // todo log
    }
    ObjectMapper mapper = new ObjectMapper().configure(
        MapperFeature.USE_ANNOTATIONS, false);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    // ignore null properties in json
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      LOG.error("Error processing JSON", e);
      // todo
    }
    //      return mapper.writeValueAsString(Class.forName(getElementInterfaceName()).newInstance
    //      ());
    //    } catch (Exception e) {
    //      LOG.error("Error while creating empty json configuration", e);
    //    }
    return StringUtils.EMPTY;
  }
}
