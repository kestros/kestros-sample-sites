#!/usr/bin/env python3
"""Generate 20 uniform placeholder player-portrait avatars (SVG, identical 400x400 viewBox).

Each is a head-and-shoulders silhouette on a solid background, varied by color so squad rows look
distinct while staying perfectly uniform in size/aspect. Written to the ACPL site assets/portraits.
"""
import os

OUT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                   "src", "content", "jcr_root", "content", "sites", "acpl", "assets", "portraits")

# 20 (background, silhouette) color pairs — muted, distinct
PALETTE = [
    ("#0B1E3F", "#5A7BA6"), ("#1B6E3A", "#7FC49B"), ("#7A1F2B", "#C98A92"),
    ("#4A2C6E", "#9C82C4"), ("#B4651A", "#E0B080"), ("#1F6F7A", "#8ECAD1"),
    ("#5C5C1F", "#B3B37A"), ("#2E4057", "#8195AA"), ("#6E1F5A", "#C084B0"),
    ("#3A5A1B", "#94B77A"), ("#7A4A1F", "#C9A784"), ("#1F3A7A", "#7F98D1"),
    ("#5A1F1F", "#B37F7F"), ("#1F5A4A", "#7FC4B3"), ("#4A4A4A", "#9E9E9E"),
    ("#6E5A1F", "#C4B37F"), ("#2B1F5A", "#8480B3"), ("#1F5A6E", "#7FB8C9"),
    ("#5A2B6E", "#B084C4"), ("#3F0B1E", "#A65A7B"),
]


def avatar(bg, fg):
    return (
        '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 400" width="400" height="400">\n'
        f'  <rect width="400" height="400" fill="{bg}"/>\n'
        f'  <circle cx="200" cy="158" r="74" fill="{fg}"/>\n'
        f'  <path d="M84 400 C84 300 140 262 200 262 C260 262 316 300 316 400 Z" fill="{fg}"/>\n'
        '</svg>\n')


if __name__ == "__main__":
    os.makedirs(OUT, exist_ok=True)
    for i, (bg, fg) in enumerate(PALETTE, start=1):
        with open(os.path.join(OUT, f"p{i}.svg"), "w") as f:
            f.write(avatar(bg, fg))
    print(f"generated {len(PALETTE)} portrait avatars in {OUT}")
