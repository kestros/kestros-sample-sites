package io.kestros.storybook.application.servlets;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kestros.cms.uiframeworks.api.exceptions.UiFrameworkRetrievalException;
import io.kestros.cms.uiframeworks.api.models.HtlTemplateFile;
import io.kestros.cms.uiframeworks.api.models.UiFramework;
import io.kestros.cms.uiframeworks.api.services.HtlTemplateFileRetrievalService;
import io.kestros.cms.uiframeworks.api.services.UiFrameworkRetrievalService;
import io.kestros.cms.uiframeworks.core.models.HtlTemplateFileResource;
import io.kestros.storybook.application.HtlTemplate;
import io.kestros.storybook.application.SelectOption;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = {Servlet.class},
    property = {"sling.servlet.paths=/bin/kestros/actions/htl-templates",
        "sling.servlet.extensions=json",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET})
public class HtlTemplateServlet extends SlingSafeMethodsServlet {

  @Reference
  private UiFrameworkRetrievalService uiFrameworkRetrievalService;

  @Reference
  private HtlTemplateFileRetrievalService htlTemplateFileRetrievalService;

  @Override
  public void doGet(@Nonnull SlingHttpServletRequest request,
      @Nonnull SlingHttpServletResponse response) {
    try {
      UiFramework uiFramework = uiFrameworkRetrievalService.getUiFramework(
          "/etc/ui-frameworks/kestros-design-framework/versions/0.0.1",
          request.getResourceResolver());
      List<HtlTemplateFile>
          templateFileList = htlTemplateFileRetrievalService.getHtlTemplatesFromUiFramework(
          uiFramework, true);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      ObjectMapper mapper = new ObjectMapper();
      List<SelectOption> options = new ArrayList<>();
      for (HtlTemplateFile templateFile : templateFileList) {
        for (HtlTemplate template : getTemplates(templateFile)) {
          options.add(
              new SelectOption(template.getTitle() + " -- " + templateFile.getResource().getPath(),
                  templateFile.getResource().getPath() + "--" + template.getName()));
        }
      }

      mapper.writeValue(response.getWriter(), options);


    } catch (UiFrameworkRetrievalException e) {
      throw new RuntimeException(e);
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    } catch (JsonGenerationException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private List<HtlTemplate> getTemplates(HtlTemplateFile htlTemplateFile) {
    final List<HtlTemplate> templates = new ArrayList<>();
    if (htlTemplateFile instanceof HtlTemplateFileResource) {
      HtlTemplateFileResource htlTemplateFileResource = (HtlTemplateFileResource) htlTemplateFile;
      try {
        final Document templateFile = Jsoup.parse(htlTemplateFile.getFileContent());
        templateFile.outputSettings().outline(true);
        templateFile.outputSettings().prettyPrint(false);
        for (final Node node : templateFile.child(0).childNodes().get(1).childNodes()) {
          if (StringUtils.isNotBlank(node.toString())) {
            final HtlTemplate template = new HtlTemplate(node, htlTemplateFileResource.getPath());
            if (template.getName() != null) {
              templates.add(template);
            }
          }
        }
      } catch (final IOException exception) {
        // todo log.
        //        LOG.warn(
        //            "Unable to get HtlTemplates for HTL Template compilation file {} due to
        //            IOException. {}",
        //            getPath(), exception.getMessage());
      }
    }
    return templates;
  }

}
