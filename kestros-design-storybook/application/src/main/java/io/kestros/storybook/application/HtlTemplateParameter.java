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

import java.util.Locale;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Node;

/**
 * HTL Template parameter details for {@link HtlTemplate} documentation.
 */
public class HtlTemplateParameter {

  private String name;
  private String description;
  private Node node;

  HtlTemplateParameter(String name, Node node) {
    this.name = name;
    this.node = node;
    this.description = "";
    final Attributes attributes = this.node.attributes();
    for (Attribute attribute : attributes) {
      if (attribute.getKey().toUpperCase(Locale.US).equals(
          ("data-" + name + "-description").toUpperCase(Locale.US))) {
        this.description = attribute.getValue();
      }
    }
  }

  /**
   * Parameter name.
   *
   * @return Parameter name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Parameter description.
   *
   * @return Parameter description.
   */
  public String getDescription() {
    return this.description;
  }

}
