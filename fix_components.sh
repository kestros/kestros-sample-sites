#!/bin/bash

# Fix breadcrumb
sed -i 's|<heading[^>]*level="h2"[^>]*text="Breadcrumb"/>|<title jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/heading" level="h1" text="Breadcrumb"/>\n                            <intro jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/richtext" text="&lt;p&gt;A single breadcrumb link component representing one step in a breadcrumb trail. Used within breadcrumbs parent component to show navigation hierarchy.&lt;/p&gt;"/>\n                            <properties-heading jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/heading" level="h2" text="Properties"/>\n                            <properties-text jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/richtext" text="&lt;p&gt;No configurable properties. Breadcrumbs are auto-generated from page hierarchy.&lt;/p&gt;"/><heading level="h2" text="Breadcrumb"/>|' src/content/jcr_root/content/sites/basic-components-sample/components/breadcrumb/.content.xml

echo "Fixed breadcrumb"
