#!/usr/bin/env python3
"""Generate per-story article pages under /content/sites/acpl/stories/<slug>, an /etc/tags/acpl tag
taxonomy (category + team tags), and tag each story page (category + the two clubs involved).

Runs off the same league JSON. Each story page is a normal kes:Page (header band + article body).
"""
import json
import os

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(BASE, "..", "acpl-league", "src", "main", "resources", "data")
ROOT = os.path.join(BASE, "src", "content", "jcr_root")
SITE = "/content/sites/acpl"
TEMPLATE = "/libs/kestros/commons/base-site-template"
THEME = "/etc/ui-frameworks/league-framework-v2/versions/0.0.1/themes/default"

stories = json.load(open(os.path.join(DATA, "stories.json")))
clubs = {c["slug"]: c for c in json.load(open(os.path.join(DATA, "clubs.json")))}
MONTHS = ["", "January", "February", "March", "April", "May", "June",
          "July", "August", "September", "October", "November", "December"]
LOREM = [
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut "
    "labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco "
    "laboris nisi ut aliquip ex ea commodo consequat.",
    "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla "
    "pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt "
    "mollit anim id est laborum.",
    "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque "
    "laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto "
    "beatae vitae dicta sunt explicabo.",
]


def esc(s):
    return (str(s).replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace('"', "&quot;").replace("'", "&#39;"))


def slugify(s):
    return "".join(c if c.isalnum() else "-" for c in s.lower()).strip("-")


def fmt_date(iso):
    y, m, d = iso.split("-")
    return f"{int(d)} {MONTHS[int(m)]} {y}"


def teams_in(story_slug):
    """Club slugs mentioned in the story slug (mw<N>-<home>-<away>)."""
    rest = story_slug.split("-", 1)[1] if "-" in story_slug else story_slug
    return [slug for slug in clubs if slug in rest]


def write_node(path, xml):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(xml)


def tag_node(title):
    return ('<?xml version="1.0" encoding="UTF-8"?>\n'
            '<jcr:root xmlns:jcr="http://www.jcp.org/jcr/1.0" '
            'xmlns:sling="http://sling.apache.org/jcr/sling/1.0" '
            f'jcr:primaryType="nt:unstructured" jcr:title="{esc(title)}" '
            'sling:resourceType="kes:Tag"/>\n')


def build_tags():
    tags_root = os.path.join(ROOT, "etc", "tags", "acpl")
    write_node(os.path.join(tags_root, ".content.xml"), tag_node("ACPL"))
    write_node(os.path.join(tags_root, "category", ".content.xml"), tag_node("Category"))
    write_node(os.path.join(tags_root, "team", ".content.xml"), tag_node("Teams"))
    cats = sorted({s["category"] for s in stories})
    for cat in cats:
        write_node(os.path.join(tags_root, "category", slugify(cat), ".content.xml"), tag_node(cat))
    for slug, c in clubs.items():
        write_node(os.path.join(tags_root, "team", slug, ".content.xml"), tag_node(c["name"]))
    print(f"tags: {len(cats)} categories + {len(clubs)} teams")


def story_page(s):
    slug = s["slug"]
    cat = s["category"]
    teams = teams_in(slug)
    tag_paths = ["/etc/tags/acpl/category/" + slugify(cat)] \
        + ["/etc/tags/acpl/team/" + t for t in teams]
    byline = f"{s['author']} · {fmt_date(s['date'])}"
    paras = "".join(
        f'<p{i} variations="[mb-3]" jcr:primaryType="nt:unstructured" '
        f'sling:resourceType="/libs/kestros/commons/components/content/text" '
        f'text="{esc(p)}"/>' for i, p in enumerate(s["body"] + LOREM))
    main = (
        f'<band layout="plain" variations="[bg-primary,text-white,py-4]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<inner layout="plain" variations="[container]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<crumb layout="plain" variations="[d-flex,gap-2,mb-2,small]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<hl href="{SITE}.html" variations="[text-white-50,text-decoration-none]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/link" text="Home"/>'
        f'<sep variations="[text-white-50]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/text" text="/"/>'
        f'<sl href="{SITE}/stories.html" variations="[text-white-50,text-decoration-none]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/link" text="Stories"/>'
        f'</crumb>'
        f'<cat variations="[text-white-50,small,text-uppercase,fw-bold,mb-1]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/text" text="{esc(cat)}"/>'
        f'<h variations="[h3,fw-bold,mb-2]" headingText="{esc(s["headline"])}" headingType="h1" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/heading"/>'
        f'<by variations="[text-white-50,mb-0]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/text" text="{esc(byline)}"/>'
        f'</inner></band>'
        f'<wrap layout="plain" variations="[container,my-5]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<row layout="plain" variations="[row]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<col layout="plain" variations="[col-lg-8]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<hero imagePath="{SITE}/assets/{esc(s["image"])}" altText="{esc(s["headline"])}" variations="[img-fluid,rounded,mb-4]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/image"/>'
        f'<lead variations="[lead,mb-4]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/text" text="{esc(s["dek"])}"/>'
        f'{paras}'
        f'<back href="{SITE}/stories.html" variations="[btn,btn-outline-primary,btn-sm,mt-3]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/link" text="← Back to all stories"/>'
        f'</col></row></wrap>'
        f'<related layout="plain" variations="[container,my-5,pt-4,border-top]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<rh variations="[section-heading,mb-4]" headingText="Related Stories" headingType="h2" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/heading"/>'
        f'<rl layout="default" kes:datasource="related-stories" story="{slug}" limit="3" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/lists/card-list"/>'
        f'</related>')
    return (
        '<?xml version="1.0" encoding="UTF-8"?>\n'
        '<jcr:root xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:kes="http://kestros.io/kes/1.0" '
        'xmlns:nt="http://www.jcp.org/jcr/nt/1.0" xmlns:sling="http://sling.apache.org/jcr/sling/1.0" '
        'jcr:primaryType="kes:Page">\n'
        f'    <jcr:content jcr:primaryType="nt:unstructured" jcr:title="{esc(s["headline"])}" '
        f'kes:template="{TEMPLATE}" kes:theme="{THEME}" '
        f'kes:tags="[{",".join(tag_paths)}]" '
        'sling:resourceType="kestros/commons/components/kestros-base-page">\n'
        '        <header jcr:primaryType="nt:unstructured" sling:resourceType="kestros/commons/components/inherited-content-area"/>\n'
        f'        <main jcr:primaryType="nt:unstructured" sling:resourceType="kestros/commons/components/content-area">{main}</main>\n'
        '        <footer jcr:primaryType="nt:unstructured" sling:resourceType="kestros/commons/components/inherited-content-area"/>\n'
        '    </jcr:content>\n'
        '</jcr:root>\n')


if __name__ == "__main__":
    build_tags()
    for s in stories:
        path = os.path.join(ROOT, "content", "sites", "acpl", "stories", s["slug"], ".content.xml")
        write_node(path, story_page(s))
        print(f"story: stories/{s['slug']} (tags: {[s['category']] + teams_in(s['slug'])})")
