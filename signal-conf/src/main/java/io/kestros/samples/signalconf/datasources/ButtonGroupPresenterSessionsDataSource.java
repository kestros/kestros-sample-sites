package io.kestros.samples.signalconf.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosButton;
import io.kestros.cms.components.basic.api.content.KestrosButtonGroup;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.LinkUtils;
import io.kestros.cms.components.basic.core.content.button.KestrosButtonImpl;
import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.cms.sitebuilding.api.models.BaseContentPage;
import io.kestros.cms.componenttypes.api.models.ComponentVariation;
import io.kestros.commons.structuredslingmodels.exceptions.NoValidAncestorException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class ButtonGroupPresenterSessionsDataSource extends BaseContainerSlingModelDataSource
    implements KestrosButtonGroup {

  private BaseContentPage containingPage;

  BaseContentPage getContainingPage() {
    if (containingPage == null) {
      try {
        BaseComponent component = getResource().adaptTo(BaseComponent.class);
        if (component != null) {
          containingPage = component.getContainingPage();
        }
      } catch (NoValidAncestorException e) {
        return null;
      }
    }
    return containingPage;
  }

  String getSessionsPath() {
    return getResource().getValueMap().get("sessionsPath", String.class);
  }

  List<BaseContentPage> getPresenterSessions() {
    List<BaseContentPage> sessions = new ArrayList<>();
    String sessionsPath = getSessionsPath();
    BaseContentPage presenterPage = getContainingPage();

    if (sessionsPath == null || presenterPage == null) {
      return sessions;
    }

    Resource sessionsResource = getResourceResolver().getResource(sessionsPath);
    if (sessionsResource == null) {
      return sessions;
    }

    BaseContentPage sessionsRoot = sessionsResource.adaptTo(BaseContentPage.class);
    if (sessionsRoot == null) {
      return sessions;
    }

    String presenterPath = presenterPage.getPath();

    for (BaseContentPage sessionPage : sessionsRoot.getChildPages()) {
      String sessionPresenter = sessionPage.getProperty("presenterPath", "");
      if (presenterPath.equals(sessionPresenter)) {
        sessions.add(sessionPage);
      }
    }

    return sessions;
  }

  @Nonnull
  @Override
  public List<KestrosButton> getButtonsElements() {
    List<KestrosButton> buttons = new ArrayList<>();
    int buttonIndex = 0;
    for (BaseContentPage session : getPresenterSessions()) {
      try {
        buttons.add(
            new KestrosButtonImpl(
                session.getDisplayTitle(),
                LinkUtils.getLink(session.getPath()),
                null,
                AnchorTarget.SAME_WINDOW,
                null, null, null, null, false,
                this,
                "button",
                "session-" + buttonIndex));
        buttonIndex++;
      } catch (Exception e) {
        // Skip buttons that fail to construct — null-safe
      }
    }
    return new ArrayList<>(buttons);
  }

  @Nonnull
  @Override
  public List<ComponentVariation> getButtonVariations() {
    return getElementVariations("button", KestrosButton.RESOURCE_TYPE);
  }
}
