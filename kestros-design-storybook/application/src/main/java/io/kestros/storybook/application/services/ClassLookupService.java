package io.kestros.storybook.application.services;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Looks up classes and class loaders for bundles.
 */
@Component(service = ClassLookupService.class)
public class ClassLookupService {

  private static final Logger LOG = LoggerFactory.getLogger(ClassLookupService.class);

  private BundleContext bundleContext;

  @Activate
  protected void activate(ComponentContext componentContext) {
    this.bundleContext = componentContext.getBundleContext();
  }

  /**
   * Retrieves the desired class from its  bundle.
   *
   * @param className the name of the class to load.
   * @return the class.
   *
   * @throws ClassNotFoundException if the class cannot be found.
   */
  public Class getClass(String className) throws ClassNotFoundException {
    return findClass(bundleContext, className);
  }

  /**
   * Retrieves the desired class loader from its  bundle.
   *
   * @param className the name of the class to get the loader for.
   * @return the class loader.
   *
   * @throws ClassNotFoundException if the class cannot be found.
   */
  public ClassLoader getClassLoader(String className) throws ClassNotFoundException {
    Bundle bundle = bundleForClass(className);
    LOG.info("Bundle: {}", bundle);
    return bundle.adapt(BundleWiring.class).getClassLoader();
  }

  /**
   * Finds the bundle that contains the desired class.
   *
   * @param className the name of the class to find.
   * @return the bundle.
   *
   * @throws ClassNotFoundException if the class cannot be found.
   */
  public Bundle bundleForClass(String className) throws ClassNotFoundException {
    if (bundleContext != null) {
      for (Bundle b : bundleContext.getBundles()) {
        if (b.getSymbolicName().contains("storybook")) {
          try {
            Class clazz = b.loadClass(className);
            if (clazz != null) {
              LOG.debug("Class found in bundle: " + b.getSymbolicName());
              return b;
            }
          } catch (ClassNotFoundException e) {
            // ignore
          }
        }
      }
    }
    throw new ClassNotFoundException("Class not found: " + className);
  }

  private Class<?> findClass(BundleContext context, String name) throws ClassNotFoundException {
    bundleForClass(name);
    return getClassLoader(name).loadClass(name);
  }
}


