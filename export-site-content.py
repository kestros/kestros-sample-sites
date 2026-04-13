#!/usr/bin/env python3
"""
Download a site's JCR content from a Sling instance and write it as
FileVault .content.xml files suitable for the filevault-package-maven-plugin.

Usage:
    python3 export-site-content.py <site-name> <output-dir> [--host HOST] [--user USER] [--password PASS]

Example:
    python3 export-site-content.py explore league-demo/content/src/content/jcr_root
"""

import argparse
import json
import os
import re
import sys
import urllib.request
import urllib.error
import base64
from xml.sax.saxutils import escape as xml_escape

# JCR namespaces used in Kestros
NAMESPACES = {
    'jcr': 'http://www.jcp.org/jcr/1.0',
    'nt': 'http://www.jcp.org/jcr/nt/1.0',
    'kes': 'http://kestros.io/kes/1.0',
    'mix': 'http://www.jcp.org/jcr/mix/1.0',
    'sling': 'http://sling.apache.org/jcr/sling/1.0',
    'rep': 'http://www.jcp.org/jcr/1.0',
}

# Properties to skip in export (runtime-only, auto-generated)
SKIP_PROPS = {
    'jcr:uuid', 'jcr:versionHistory', 'jcr:predecessors', 'jcr:baseVersion',
    'jcr:isCheckedOut', 'jcr:createdBy', 'jcr:created',
    'jcr:lastModifiedBy', 'jcr:lastModified',
    'kes:lastModified', 'kes:lastModifiedBy',
    'kes:publicationStatus', 'kes:publicationStatusLastChanged', 'kes:lastPublishedBy',
    'jcr:lockIsDeep', 'jcr:lockOwner', 'jcr:mixinTypes',
}

# Properties that are arrays of typed values
MULTI_VALUE_HINT = re.compile(r'^\{(\w+)\}(.*)$')


def fetch_json(url, user, password):
    """Fetch JSON from a URL with basic auth."""
    auth = base64.b64encode(f'{user}:{password}'.encode()).decode()
    req = urllib.request.Request(url, headers={'Authorization': f'Basic {auth}'})
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        print(f'  WARN: {url} returned {e.code}', file=sys.stderr)
        return None


def strip_type_hint(value):
    """Remove Composum type hints like {Name}, {Date}, {Reference} etc."""
    if isinstance(value, str):
        m = MULTI_VALUE_HINT.match(value)
        if m:
            return m.group(2)
    return value


def format_xml_value(key, value):
    """Format a JCR property value for .content.xml."""
    if isinstance(value, list):
        # Multi-value property
        items = [strip_type_hint(str(v)) for v in value]
        return '"[' + ','.join(items) + ']"'

    val = strip_type_hint(str(value))
    return '"' + xml_escape(val).replace('"', '&quot;') + '"'


def is_child_node(key, value):
    """Check if a JSON key/value represents a child JCR node."""
    return isinstance(value, dict) and not key.startswith('::')


def write_node(f, node_data, indent=0, node_name=None):
    """Write a JCR node as XML element."""
    prefix = '    ' * indent
    tag = node_name or 'jcr:root'

    # Collect properties (non-dict values) and children (dict values)
    props = {}
    children = []

    for key, value in node_data.items():
        if key == 'rep:policy':
            continue  # Skip ACLs
        if is_child_node(key, value):
            children.append((key, value))
        elif key not in SKIP_PROPS:
            props[key] = value

    # Write opening tag with properties
    f.write(f'{prefix}<{tag}\n')
    for key, value in sorted(props.items()):
        if value is None or value == '':
            continue
        formatted = format_xml_value(key, value)
        f.write(f'{prefix}        {key}={formatted}\n')

    if children:
        f.write(f'{prefix}>\n')
        for child_name, child_data in children:
            write_node(f, child_data, indent + 1, child_name)
        f.write(f'{prefix}</{tag}>\n')
    else:
        f.write(f'{prefix}/>\n')


def write_content_xml(filepath, node_data):
    """Write a .content.xml file for a JCR node."""
    os.makedirs(os.path.dirname(filepath), exist_ok=True)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write('<?xml version="1.0" encoding="UTF-8"?>\n')

        # Build namespace declarations
        ns_attrs = []
        for prefix, uri in sorted(NAMESPACES.items()):
            ns_attrs.append(f'xmlns:{prefix}="{uri}"')

        f.write('<jcr:root ' + '\n          '.join(ns_attrs) + '\n')

        # Write root properties
        props = {}
        children = []
        for key, value in node_data.items():
            if key == 'rep:policy':
                continue
            if is_child_node(key, value):
                children.append((key, value))
            elif key not in SKIP_PROPS:
                props[key] = value

        for key, value in sorted(props.items()):
            if value is None or value == '':
                continue
            formatted = format_xml_value(key, value)
            f.write(f'          {key}={formatted}\n')

        if children:
            f.write('>\n')
            for child_name, child_data in children:
                write_node(f, child_data, 1, child_name)
            f.write('</jcr:root>\n')
        else:
            f.write('/>\n')


def export_site(site_name, output_root, host, user, password):
    """Export a site's content to FileVault .content.xml files."""
    base_url = f'{host}/bin/cpm/nodes/node.json/content/sites/{site_name}'

    print(f'Exporting {site_name}...')

    # Download the full site tree
    data = fetch_json(base_url, user, password)
    if data is None:
        print(f'  ERROR: Could not fetch site {site_name}', file=sys.stderr)
        return False

    # Write the site root .content.xml
    site_dir = os.path.join(output_root, 'content', 'sites', site_name)
    write_content_xml(os.path.join(site_dir, '.content.xml'), data)

    # Count what we wrote
    node_count = 1

    # Write child page .content.xml files
    for key, value in data.items():
        if is_child_node(key, value) and key not in ('jcr:content', 'rep:policy'):
            page_dir = os.path.join(site_dir, key)
            write_content_xml(os.path.join(page_dir, '.content.xml'), value)
            node_count += 1

            # Write grandchild pages
            for subkey, subvalue in value.items():
                if is_child_node(subkey, subvalue) and subkey not in ('jcr:content', 'rep:policy'):
                    subpage_dir = os.path.join(page_dir, subkey)
                    write_content_xml(os.path.join(subpage_dir, '.content.xml'), subvalue)
                    node_count += 1

    print(f'  Wrote {node_count} .content.xml files to {site_dir}')
    return True


def main():
    parser = argparse.ArgumentParser(description='Export Sling site content to FileVault format')
    parser.add_argument('site', help='Site name (e.g., explore)')
    parser.add_argument('output', help='Output jcr_root directory')
    parser.add_argument('--host', default='http://192.168.86.216:8001', help='Sling host')
    parser.add_argument('--user', default='admin', help='Username')
    parser.add_argument('--password', default='kestros-dev000', help='Password')

    args = parser.parse_args()

    success = export_site(args.site, args.output, args.host, args.user, args.password)
    sys.exit(0 if success else 1)


if __name__ == '__main__':
    main()
