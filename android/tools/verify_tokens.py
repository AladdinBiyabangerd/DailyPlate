"""Check Android palette parity and the foreground/background pairs in use."""
from pathlib import Path
import re
import xml.etree.ElementTree as ET


def check(root: Path) -> None:
    css = (root / 'design-concepts/tokens.css').read_text()
    tokens = dict(re.findall(r'--([\w-]+):\s*(#[\da-fA-F]{6})', css))
    colors = {node.attrib['name']: node.text for node in ET.parse(
        root / 'android/app/src/main/res/values/colors.xml').getroot()}
    for name, color in colors.items():
        assert color.upper() == tokens[name.replace('_', '-')].upper(), name

    resources = root / 'android/app/src/main/res'
    base_strings = {node.attrib['name'] for node in ET.parse(resources / 'values/strings.xml').getroot()}
    for locale in ('az', 'en', 'ru'):
        strings = list(ET.parse(resources / f'values-{locale}/strings.xml').getroot())
        assert {node.attrib['name'] for node in strings} == base_strings, locale
        assert all(node.text and node.text.strip() for node in strings), locale
        print(f'PASS complete {locale} string resources: {len(strings)} keys')

    def luminance(color):
        channels = [int(color[i:i + 2], 16) / 255 for i in (1, 3, 5)]
        linear = [c / 12.92 if c <= .04045 else ((c + .055) / 1.055) ** 2.4 for c in channels]
        return sum(c * w for c, w in zip(linear, (.2126, .7152, .0722)))

    for fg, bg in [('simple_text', 'simple_bg'), ('simple_muted', 'simple_bg'),
                   ('simple_surface', 'simple_primary'), ('simple_primary', 'simple_surface'),
                   ('simple_text', 'simple_surface'), ('simple_muted', 'simple_surface')]:
        a, b = sorted((luminance(colors[fg]), luminance(colors[bg])))
        contrast = (b + .05) / (a + .05)
        assert contrast >= 4.5, (fg, bg, contrast)
        print(f'PASS contrast {fg}/{bg}: {contrast:.2f}:1')


if __name__ == '__main__':
    check(Path(__file__).resolve().parents[2])
