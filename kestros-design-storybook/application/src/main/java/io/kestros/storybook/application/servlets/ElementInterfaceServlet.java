package io.kestros.storybook.application.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.kestros.storybook.application.SelectOption;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {Servlet.class},
    property = {"sling.servlet.paths=/bin/kestros/actions/element-interfaces",
        "sling.servlet.extensions=json",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class ElementInterfaceServlet extends SlingSafeMethodsServlet {

  private static final Logger LOG = LoggerFactory.getLogger(
      ElementInterfaceServlet.class);
  private ComponentContext componentContext;
  private List<Bundle> storybookBundles = new ArrayList<>();

  private List<Class> elementInterfaces = new ArrayList<>();

  @Override
  protected void doGet(@Nonnull SlingHttpServletRequest request,
      @Nonnull SlingHttpServletResponse response) {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    List<SelectOption> options = new ArrayList<>();
    updateBundles();
    for (Class clazz : elementInterfaces) {
      options.add(new SelectOption(clazz.getSimpleName(), clazz.getName()));
    }
    ObjectMapper mapper = new ObjectMapper();
    try {
      response.getWriter().write(mapper.writeValueAsString(options));
    } catch (Exception e) {
      LOG.error("Unable to write response.", e);
    }
  }


  private void updateBundles() {
    LOG.info("Activating ElementInterfaceDatasourceServlet");
    if (componentContext == null) {
      LOG.info("Component context is null");
      return;
    }
    for (Bundle bundle : componentContext.getBundleContext().getBundles()) {
      LOG.debug("Bundle: {}", bundle.getSymbolicName());
      if (bundle.getSymbolicName().startsWith("io.kestros.storybook") && bundle.getSymbolicName().contains("frontend")) {
        LOG.info("Adding bundle: {}", bundle.getSymbolicName());
        storybookBundles.add(bundle);
      }
    }
    LOG.info("Found {} kestros bundles", storybookBundles.size());
    for (Bundle bundle : storybookBundles) {
      LOG.info("Checking Bundle {} for element model interfaces", bundle.getSymbolicName());
      // get all classes in a bundle that end with "ElementModel"

      BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

      if (bundleWiring != null) {
        bundleWiring.listResources("/", "*.class", BundleWiring.LISTRESOURCES_RECURSE).forEach(
            resource -> {
              String className = resource.replace("/", ".").replace(".class", "");
              try {
                if (className.endsWith("Atom") || className.endsWith("Molecule") || className.endsWith("Organism")) {
                  Class clazz = bundle.loadClass(className);
                  if (clazz.isInterface()) {
                    LOG.info("Found element model interface: {}", clazz.getName());
                    // if elements does not contain a class with the same symbolic name
                    if (elementInterfaces.stream().noneMatch(
                        elementInterface -> elementInterface.getCanonicalName().equals(
                            clazz.getCanonicalName()))) {
                      elementInterfaces.add(clazz);
                    }
                  }
                }
              } catch (ClassNotFoundException e) {
                LOG.error("Unable to load class: {}", className);
              } catch (Exception e) {
                LOG.error("Unable to load class: {}", className);
              }
            });
      }
    }
  }

  @Activate
  @SuppressFBWarnings({"MSF_MUTABLE_SERVLET_FIELD", "MTIA_SUSPECT_SERVLET_INSTANCE_FIELD"})
  protected void activate(ComponentContext context) {
    componentContext = context;
  }


}
