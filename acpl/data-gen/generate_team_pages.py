#!/usr/bin/env python3
"""Generate the 12 ACPL team pages from the harborside template + league JSON.

Each team page mirrors the harborside reference: team header (crest / name / subtitle),
eight season-stat cards, and a squad datasource table (kes:datasource="squad" club="<slug>").
Header + stats are authored from the JSON at build time; the squad roster is datasource-driven.
"""
import json
import os

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(BASE, "..", "acpl-league", "src", "main", "resources", "data")
TEAMS = os.path.join(BASE, "src", "content", "jcr_root", "content", "sites", "acpl", "teams")
SITE = "/content/sites/acpl"

clubs = {c["slug"]: c for c in json.load(open(os.path.join(DATA, "clubs.json")))}
standings = {r["club"]: r for r in json.load(open(os.path.join(DATA, "standings.json")))}


def ordinal(n):
    if 10 <= n % 100 <= 20:
        suf = "th"
    else:
        suf = {1: "st", 2: "nd", 3: "rd"}.get(n % 10, "th")
    return f"{n}{suf}"


def esc(s):
    return (str(s).replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace('"', "&quot;"))


def stat_card(node, value, label):
    return (
        f'<{node} layout="plain" variations="[col-6,col-sm-4,col-md-3,col-lg-auto,mb-3]" '
        f'jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<c layout="plain" variations="[card,text-center,border-0,shadow-sm,px-3,py-2]" '
        f'jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<v variations="[player-stat-val]" jcr:primaryType="nt:unstructured" '
        f'sling:resourceType="/libs/kestros/commons/components/content/text" text="{esc(value)}"/>'
        f'<l variations="[player-stat-label]" jcr:primaryType="nt:unstructured" '
        f'sling:resourceType="/libs/kestros/commons/components/content/text" text="{esc(label)}"/>'
        f'</c></{node}>')


def team_page(slug):
    c = clubs[slug]
    s = standings[slug]
    name = c["name"]
    sub1 = f"Atlantic Coast Premier League — {ordinal(s['pos'])} Place"
    sub2 = f"{c['stadium']} · Manager: {c['mgr']} · Founded {c['founded']}"
    cards = "".join([
        stat_card("st0", s["p"], "Played"),
        stat_card("st1", s["w"], "Won"),
        stat_card("st2", s["d"], "Drawn"),
        stat_card("st3", s["l"], "Lost"),
        stat_card("st4", s["gf"], "GF"),
        stat_card("st5", s["ga"], "GA"),
        stat_card("st6", s["gd"], "GD"),
        stat_card("st7", s["pts"], "Points"),
    ])
    main = (
        f'<teamhdr layout="plain" variations="[team-header]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<inner layout="plain" variations="[container]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<flex layout="plain" variations="[d-flex,align-items-center,gap-4]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<crest imagePath="{SITE}/assets/crests/{slug}.svg" altText="{esc(name)} crest" href="{SITE}/teams/{slug}.html" variations="[crest-xxl]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/image"/>'
        f'<info layout="plain" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<h variations="[team-name,mb-1]" headingText="{esc(name)}" headingType="h1" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/heading"/>'
        f'<s1 variations="[team-sub,mb-1]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/text" text="{esc(sub1)}"/>'
        f'<s2 variations="[team-sub,mb-0]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/text" text="{esc(sub2)}"/>'
        f'</info></flex></inner></teamhdr>'
        f'<wrap layout="plain" variations="[container,my-4]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<statsh variations="[section-heading,mb-3]" headingText="Season Statistics" headingType="h2" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/heading"/>'
        f'<statsrow layout="plain" variations="[row,mb-4]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">{cards}</statsrow>'
        f'<grid layout="plain" variations="[row,g-4]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<col8 layout="plain" variations="[col-lg-8]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<squadh variations="[section-heading,mb-3]" headingText="Squad" headingType="h2" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/heading"/>'
        f'<squadcard layout="plain" variations="[card,border-0,shadow-sm,mb-4]" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/structure/container">'
        f'<table variations="[table,table-hover,align-middle,mb-0]" kes:datasource="squad" club="{slug}" jcr:primaryType="nt:unstructured" sling:resourceType="/libs/kestros/commons/components/content/table"/>'
        f'</squadcard></col8></grid></wrap>')
    return (
        '<?xml version="1.0" encoding="UTF-8"?>\n'
        '<jcr:root xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:kes="http://kestros.io/kes/1.0" '
        'xmlns:nt="http://www.jcp.org/jcr/nt/1.0" xmlns:sling="http://sling.apache.org/jcr/sling/1.0" '
        'jcr:primaryType="kes:Page">\n'
        f'    <jcr:content jcr:primaryType="nt:unstructured" jcr:title="{esc(name)}" '
        'kes:template="/libs/kestros/commons/base-site-template" '
        'kes:theme="/etc/ui-frameworks/league-framework-v2/versions/0.0.1/themes/default" '
        'sling:resourceType="kestros/commons/components/kestros-base-page">\n'
        '        <header jcr:primaryType="nt:unstructured" sling:resourceType="kestros/commons/components/inherited-content-area"/>\n'
        f'        <main jcr:primaryType="nt:unstructured" sling:resourceType="kestros/commons/components/content-area">{main}</main>\n'
        '        <footer jcr:primaryType="nt:unstructured" sling:resourceType="kestros/commons/components/inherited-content-area"/>\n'
        '    </jcr:content>\n'
        '</jcr:root>\n')


if __name__ == "__main__":
    for slug in clubs:
        d = os.path.join(TEAMS, slug)
        os.makedirs(d, exist_ok=True)
        with open(os.path.join(d, ".content.xml"), "w") as f:
            f.write(team_page(slug))
        print(f"generated teams/{slug} ({clubs[slug]['name']}, {ordinal(standings[slug]['pos'])})")
