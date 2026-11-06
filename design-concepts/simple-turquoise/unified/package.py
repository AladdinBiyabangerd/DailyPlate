"""Package only the current uniform designs; preserve legacy references in the repo."""
import hashlib
import json
from pathlib import Path
import zipfile

HERE=Path(__file__).resolve().parent
DEST=HERE.parents[1]


def files():
    return sorted(p for p in HERE.rglob('*') if p.is_file() and '__pycache__' not in p.parts and p.name not in {'MANIFEST.json','.DS_Store'})


def package():
    paths=files()
    screens=json.loads((HERE/'screens.json').read_text())
    manifest={'date':'2026-11-06','scope':'uniform-design-artifacts-not-runtime-acceptance','entrypoint':'index.html','sheets':len(screens),'panels':sum(len(s['panels']) for s in screens),'files':[{'path':str(p.relative_to(HERE)),'sha256':hashlib.sha256(p.read_bytes()).hexdigest()} for p in paths]}
    payload=json.dumps(manifest,ensure_ascii=False,indent=2)+'\n'
    (HERE/'MANIFEST.json').write_text(payload)
    destination=DEST/'qida-turquoise-designs-final.zip'
    with zipfile.ZipFile(destination,'w',zipfile.ZIP_DEFLATED) as z:
        for p in paths+[HERE/'MANIFEST.json']:
            z.write(p,'QIDA-final/'+str(p.relative_to(HERE)))
    with zipfile.ZipFile(destination) as z:
        assert z.testzip() is None
        assert len(z.namelist())==len(paths)+1
        for record in manifest['files']:
            assert hashlib.sha256(z.read('QIDA-final/'+record['path'])).hexdigest()==record['sha256']
    (DEST/'qida-turquoise-designs-final.manifest.json').write_text(payload)
    print(f'{destination}: {len(paths)+1} entries; every SHA-256 verified')

if __name__=='__main__': package()
