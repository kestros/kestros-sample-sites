/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.kestros.storybook.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

/**
 * Structured HTL template tags, so that they can be compiled and managed by VendorLibraries and
 * UiFrameworks.
 */
public class HtlTemplate {

  public static final String ATTRIBUTE_DATA_SLY_TEMPLATE = "data-sly-template.";

  private final Node node;
  private final String sourcePath;

  private static final String CALL_VARIABLES_INDENT = "     ";

  /**
   * Constructs an HtlTemplate object.
   *
   * @param node HTML Node which created the template.
   * @param sourcePath HtlTemplateFile path.
   */
  public HtlTemplate(final Node node, final String sourcePath) {
    this.node = node;
    this.sourcePath = sourcePath;
  }

  /**
   * Details of template parameters, for automated documentation purposes.
   *
   * @return Details of template parameters, for automated documentation purposes.
   */
  public List<HtlTemplateParameter> getTemplateParameters() {
    List<HtlTemplateParameter> parameters = new ArrayList<>();
    for (String parameter : getParameterNames()) {
      parameters.add(new HtlTemplateParameter(parameter, this.node));
    }
    return parameters;
  }

  /**
   * Retrieves a specified parameter.
   *
   * @param parameterName Parameter to retrieve.
   * @return A specified parameter.
   */
  @Nullable
  public HtlTemplateParameter getTemplateParameter(String parameterName) {
    for (HtlTemplateParameter parameter : getTemplateParameters()) {
      if (parameter.getName().equalsIgnoreCase(parameterName)) {
        return parameter;
      }
    }
    return null;
  }

  /**
   * HTL Template's origination file.
   *
   * @return HTL Template's origination file.
   */
  public String getSourcePath() {
    return this.sourcePath;
  }

  /**
   * HTL Template name.
   *
   * @return HTL Template name.
   */
  public String getName() {
    final Attributes attributes = this.node.attributes();

    for (final Attribute attribute : attributes) {
      final String key = attribute.getKey();
      if (key.startsWith(ATTRIBUTE_DATA_SLY_TEMPLATE)) {
        return key.split(ATTRIBUTE_DATA_SLY_TEMPLATE)[1];
      }
    }
    return null;
  }

  /**
   * Title of the current template, or template name if blank.
   *
   * @return Title of the current template.
   */
  public String getTitle() {
    final Attributes attributes = this.node.attributes();
    final String title = attributes.get("data-title");
    if (StringUtils.isNotBlank(title)) {
      return title;
    }
    return getName();
  }

  /**
   * Description of the current template.
   *
   * @return Description of the current template.
   */
  public String getDescription() {
    final Attributes attributes = this.node.attributes();
    final String description = attributes.get("data-description");
    if (StringUtils.isNotBlank(description)) {
      return description;
    }
    return StringUtils.EMPTY;
  }

  /**
   * Full HTML output of the HTL Template.
   *
   * @return Full HTML output of the HTL Template.
   */
  public String getOutput() {
    return this.node.toString();
  }

  /**
   * HTML Output of the current template after it is called from the implementing script.
   *
   * @return HTML Output of the current template after it is called from the implementing script.
   */
  public String getHtmlOutput() {
    final StringBuilder htmlOutputStringBuilder = new StringBuilder();
    for (final Node child : this.node.childNodes()) {
      String childHtml = child.toString();
      childHtml = childHtml.replaceAll("data-", "\ndata-");
      htmlOutputStringBuilder.append(childHtml);
    }

    String htmlOutput = htmlOutputStringBuilder.toString();
    if (htmlOutput.startsWith("\n")) {
      htmlOutput = htmlOutput.replaceFirst("\n", "");
    }
    final Document htmlOutputDocument = Jsoup.parseBodyFragment(htmlOutput);
    htmlOutputDocument.outputSettings().outline(true);
    htmlOutputDocument.outputSettings().prettyPrint(false);

    return htmlOutputDocument.body().html();
  }

  /**
   * List of variables required to implement the current Template.
   *
   * @return List of variables required to implement the current Template.
   */
  public List<String> getParameterNames() {
    final Attributes attributes = this.node.attributes();

    String value = "";
    for (final Attribute attribute : attributes) {
      final String key = attribute.getKey();
      if (key.startsWith(ATTRIBUTE_DATA_SLY_TEMPLATE)) {
        value = attribute.getValue();
      }
    }

    value = value.replaceAll("\\$", "");
    value = value.replaceAll("\\{", "");
    value = value.replaceAll("\\}", "");
    value = value.replaceAll("@", "");
    value = value.replaceAll(" ", "");

    return Arrays.asList(value.split(","));
  }

  /**
   * Sample usage script.
   *
   * @return Sample usage script.
   */
  public String getSampleUsage() {
    final StringBuilder sampleUsage = new StringBuilder();
    sampleUsage.append("<sly data-sly-call=\"${templates.");
    sampleUsage.append(getName());
    sampleUsage.append(" @");
    String prefix = "\n" + CALL_VARIABLES_INDENT;
    for (final HtlTemplateParameter parameter : getTemplateParameters()) {
      sampleUsage.append(prefix);
      prefix = ",\n" + CALL_VARIABLES_INDENT;
      sampleUsage.append(parameter.getName());
      sampleUsage.append("=");
      final String value = "my" + parameter.getName().substring(0, 1).toUpperCase(Locale.ENGLISH)
                           + parameter.getName().substring(1);
      sampleUsage.append(value);
    }
    sampleUsage.append("}\" />");
    return sampleUsage.toString();
  }

  /**
   * Font Awesome Icon class.
   *
   * @return Font Awesome Icon class.
   */
  public String getFontAwesomeIcon() {
    final Attributes attributes = this.node.attributes();
    final String iconClass = attributes.get("data-fontawesome-icon");
    if (StringUtils.isNotBlank(iconClass)) {
      return iconClass;
    }
    return "fas fa-code";
  }
}
